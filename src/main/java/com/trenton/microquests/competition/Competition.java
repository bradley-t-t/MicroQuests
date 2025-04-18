package com.trenton.microquests.competition;

import com.trenton.coreapi.util.MessageUtils;
import com.trenton.microquests.MicroQuests;
import com.trenton.microquests.competition.quests.Quest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Competition {
    private final MicroQuests plugin;
    private final Quest quest;
    private final Map<UUID, Integer> progress;
    private final long startTime;
    private boolean active;

    public Competition(MicroQuests plugin, Quest quest) {
        this.plugin = plugin;
        this.quest = quest;
        this.progress = new HashMap<>();
        this.startTime = System.currentTimeMillis();
        this.active = false;
    }

    public void start() {
        active = true;
        MessageUtils.broadcast(plugin, plugin.getConfigManager().getMessages(), "competition-start", quest);
        MessageUtils.sendTitle(plugin, plugin.getConfigManager().getMessages(), "competition-start-title", "competition-start-subtitle", quest);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (active) end(null);
            }
        }.runTaskLater(plugin, plugin.getConfig().getLong("max-quest-time") * 20);
    }

    public void end(Player winner) {
        if (!active) return;
        active = false;
        if (winner != null) {
            MessageUtils.broadcast(plugin, plugin.getConfigManager().getMessages(), "competition-win", quest, winner);
            MessageUtils.sendTitle(plugin, plugin.getConfigManager().getMessages(), "competition-win-title", "competition-win-subtitle", quest, winner);
            plugin.getRewardManager().rewardWinner(winner, quest);
        } else {
            MessageUtils.broadcast(plugin, plugin.getConfigManager().getMessages(), "competition-expired");
        }
        progress.clear();
    }

    public Quest getQuest() {
        return quest;
    }

    public long getStartTime() {
        return startTime;
    }

    public void incrementProgress(UUID uuid) {
        if (!active) return;
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        progress.merge(uuid, 1, Integer::sum);
        MessageUtils.sendActionBar(plugin, plugin.getConfigManager().getMessages(), player, "progress-update", quest, progress.get(uuid));
        if (progress.get(uuid) >= quest.getAmount()) {
            end(player);
        }
    }

    public int getProgress(UUID uuid) {
        return progress.getOrDefault(uuid, 0);
    }

    public boolean isActive() {
        return active;
    }
}