package com.trenton.microquests.managers;

import com.trenton.coreapi.api.ManagerBase;
import com.trenton.microquests.MicroQuests;
import com.trenton.microquests.competition.quests.Quest;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;

public class RewardManager implements ManagerBase {
    private MicroQuests plugin;
    private List<String> victoryCommands;
    private Random random;

    @Override
    public void init(Plugin plugin) {
        this.plugin = (MicroQuests) plugin;
        this.victoryCommands = plugin.getConfig().getStringList("rewards.on-victory");
        this.random = new Random();
    }

    @Override
    public void shutdown() {}

    public void rewardWinner(Player player, Quest quest) {
        boolean commandsExecuted = false;

        for (String cmd : victoryCommands) {
            try {
                if (cmd.startsWith("/")) {
                    cmd = cmd.substring(1);
                }
                cmd = cmd.replace("{player}", player.getName())
                        .replace("{quest}", quest.getObjective())
                        .replace("{amount}", String.valueOf(quest.getAmount()));
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                plugin.getLogger().info("Executed reward command for " + player.getName() + ": " + cmd);
                commandsExecuted = true;
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to execute reward command for " + player.getName() + ": " + cmd + " (" + e.getMessage() + ")");
            }
        }

        if (!commandsExecuted) {
            plugin.getLogger().info("No valid victory commands executed for " + player.getName() + ", applying fallback rewards");
            // XP
            String xpRange = plugin.getConfig().getString("rewards.fallback.xp", "0");
            int xp = parseRange(xpRange);
            if (xp > 0) {
                player.giveExp(xp);
                plugin.getLogger().info("Gave " + xp + " XP to " + player.getName());
            }

            List<?> itemList = plugin.getConfig().getList("rewards.fallback.items", List.of());
            for (Object obj : itemList) {
                if (obj instanceof java.util.Map) {
                    @SuppressWarnings("unchecked")
                    java.util.Map<String, Object> itemMap = (java.util.Map<String, Object>) obj;
                    Material material = Material.getMaterial((String) itemMap.get("material"));
                    if (material != null) {
                        int amount = parseRange((String) itemMap.get("amount"));
                        player.getInventory().addItem(new ItemStack(material, amount));
                        plugin.getLogger().info("Gave " + amount + " " + material.name() + " to " + player.getName());
                    }
                }
            }

            List<?> buffList = plugin.getConfig().getList("rewards.fallback.buffs", List.of());
            for (Object obj : buffList) {
                if (obj instanceof java.util.Map) {
                    @SuppressWarnings("unchecked")
                    java.util.Map<String, Object> buffMap = (java.util.Map<String, Object>) obj;
                    PotionEffectType effect = PotionEffectType.getByName((String) buffMap.get("effect"));
                    if (effect != null) {
                        int duration = ((Number) buffMap.get("duration")).intValue() * 20;
                        int amplifier = ((Number) buffMap.get("amplifier")).intValue();
                        player.addPotionEffect(new PotionEffect(effect, duration, amplifier));
                        plugin.getLogger().info("Applied " + effect.getName() + " (duration: " + duration + ", amplifier: " + amplifier + ") to " + player.getName());
                    }
                }
            }
        }
    }

    private int parseRange(String range) {
        try {
            if (range.contains("-")) {
                String[] parts = range.split("-");
                int min = Integer.parseInt(parts[0]);
                int max = Integer.parseInt(parts[1]);
                return min + random.nextInt(max - min + 1);
            } else {
                return Integer.parseInt(range);
            }
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}