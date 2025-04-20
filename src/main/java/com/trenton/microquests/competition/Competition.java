package com.trenton.microquests.competition;

import com.trenton.coreapi.util.MessageUtils;
import com.trenton.microquests.MicroQuests;
import com.trenton.microquests.competition.quests.Quest;
import com.trenton.microquests.managers.RewardManager;
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
        MessageUtils.broadcast(plugin, plugin.getCoreAPI().getMessages(), "competition-start", quest.getObjective());
        MessageUtils.sendTitle(plugin, plugin.getCoreAPI().getMessages(), "competition-start-title", "competition-start-subtitle", quest.getObjective());
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
            MessageUtils.broadcast(plugin, plugin.getCoreAPI().getMessages(), "competition-win", winner.getName(), quest.getObjective());
            MessageUtils.sendTitle(plugin, plugin.getCoreAPI().getMessages(), "competition-win-title", "competition-win-subtitle", winner.getName(), quest.getObjective());
            ((RewardManager) plugin.getCoreAPI().getManager("RewardManager")).rewardWinner(winner, quest);
        } else {
            MessageUtils.broadcast(plugin, plugin.getCoreAPI().getMessages(), "competition-expired");
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
        MessageUtils.sendActionBar(plugin.getCoreAPI().getMessages(), player, "progress-update", quest.getObjective(), progress.get(uuid), quest.getAmount());
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