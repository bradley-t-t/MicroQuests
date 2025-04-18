package com.trenton.microquests.competition;

import com.trenton.coreapi.util.MessageUtils;
import com.trenton.microquests.MicroQuests;
import com.trenton.microquests.competition.quests.CraftQuest;
import com.trenton.microquests.competition.quests.GatherQuest;
import com.trenton.microquests.competition.quests.KillQuest;
import com.trenton.microquests.competition.quests.Quest;
import com.trenton.microquests.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuestGenerator {
    private final MicroQuests plugin;
    private final ConfigManager configManager;
    private final Random random;

    public QuestGenerator(MicroQuests plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.random = new Random();
    }

    public Quest generateQuest() {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        boolean underground = players.stream().anyMatch(p -> p.getLocation().getY() < 50);

        List<String> possibleTypes = new ArrayList<>();
        if (!configManager.getValidKillMobs().isEmpty()) possibleTypes.add("kill");
        if (!configManager.getValidGatherItems().isEmpty()) possibleTypes.add("gather");
        if (!configManager.getValidCraftItems().isEmpty()) possibleTypes.add("craft");

        if (possibleTypes.isEmpty()) {
            plugin.getLogger().warning("No valid quest configurations found. Cannot generate quest.");
            return null;
        }

        List<String> weightedTypes = new ArrayList<>();
        if (underground) {
            if (possibleTypes.contains("kill")) {
                weightedTypes.add("kill");
                weightedTypes.add("kill");
            }
            if (possibleTypes.contains("gather")) weightedTypes.add("gather");
            if (possibleTypes.contains("craft")) weightedTypes.add("craft");
        } else {
            if (possibleTypes.contains("gather")) {
                weightedTypes.add("gather");
                weightedTypes.add("gather");
            }
            if (possibleTypes.contains("craft")) weightedTypes.add("craft");
            if (possibleTypes.contains("kill")) weightedTypes.add("kill");
        }

        if (weightedTypes.isEmpty()) {
            plugin.getLogger().warning("No quests available for the current context.");
            return null;
        }

        String type = weightedTypes.get(random.nextInt(weightedTypes.size()));

        switch (type) {
            case "kill":
                return generateKillQuest();
            case "gather":
                return generateGatherQuest();
            case "craft":
                return generateCraftQuest();
            default:
                return null;
        }
    }

    private Quest generateKillQuest() {
        List<EntityType> mobs = configManager.getValidKillMobs();
        if (mobs.isEmpty()) return null;
        EntityType mob = mobs.get(random.nextInt(mobs.size()));
        int minAmount = configManager.getKillMinAmount();
        int maxAmount = configManager.getKillMaxAmount();
        int amount = minAmount + random.nextInt(maxAmount - minAmount + 1);
        String name = MessageUtils.formatEnumName(mob.name());
        String plural = amount > 1 ? "s" : "";
        String objective = "Kill " + amount + " " + name + plural;
        return new KillQuest(mob, amount, objective);
    }

    private Quest generateGatherQuest() {
        List<Material> items = configManager.getValidGatherItems();
        if (items.isEmpty()) return null;
        Material item = items.get(random.nextInt(items.size()));
        int minAmount = configManager.getGatherMinAmount();
        int maxAmount = configManager.getGatherMaxAmount();
        int amount = minAmount + random.nextInt(maxAmount - minAmount + 1);
        String name = MessageUtils.formatEnumName(item.name());
        String plural = amount > 1 ? "s" : "";
        String objective = "Gather " + amount + " " + name + plural;
        return new GatherQuest(item, amount, objective);
    }

    private Quest generateCraftQuest() {
        List<Material> items = configManager.getValidCraftItems();
        if (items.isEmpty()) return null;
        Material item = items.get(random.nextInt(items.size()));
        int minAmount = configManager.getCraftMinAmount();
        int maxAmount = configManager.getCraftMaxAmount();
        int amount = minAmount + random.nextInt(maxAmount - minAmount + 1);
        String name = MessageUtils.formatEnumName(item.name());
        String plural = amount > 1 ? "s" : "";
        String objective = "Craft " + amount + " " + name + plural;
        return new CraftQuest(item, amount, objective);
    }
}