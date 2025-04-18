package com.trenton.microquests.managers;

import com.trenton.microquests.MicroQuests;
import com.trenton.microquests.competition.Competition;
import com.trenton.microquests.competition.QuestGenerator;
import com.trenton.microquests.interfaces.ManagerBase;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class CompetitionManager implements ManagerBase {
    private MicroQuests plugin;
    private Competition activeCompetition;
    private ConfigManager configManager;

    @Override
    public void init(Plugin plugin) {
        this.plugin = (MicroQuests) plugin;
        this.configManager = this.plugin.getConfigManager();
        startInterval();
    }

    @Override
    public void shutdown() {
        if (activeCompetition != null && activeCompetition.isActive()) {
            activeCompetition.end(null);
        }
    }

    private void startInterval() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (activeCompetition == null || !activeCompetition.isActive()) {
                    int onlinePlayers = Bukkit.getOnlinePlayers().size();
                    int minPlayers = configManager.getConfig().getInt("min-players");
                    if (onlinePlayers >= minPlayers) {
                        activeCompetition = new Competition(plugin, new QuestGenerator(plugin).generateQuest());
                        activeCompetition.start();
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 1200); // Check every 60s
    }

    public Competition getActiveCompetition() {
        return activeCompetition;
    }
}