package com.trenton.microquests.managers;

import com.trenton.coreapi.annotations.CoreManager;
import com.trenton.microquests.MicroQuests;
import com.trenton.microquests.competition.Competition;
import com.trenton.microquests.competition.QuestGenerator;
import com.trenton.microquests.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

@CoreManager(name = "CompetitionManager")
public class CompetitionManager {
    private MicroQuests plugin;
    private Competition activeCompetition;
    private ConfigManager configManager;

    public void init(MicroQuests plugin) {
        this.plugin = plugin;
        this.configManager = (ConfigManager) plugin.getCoreAPI().getManager("ConfigManager");
        startInterval();
    }

    public void shutdown() {
        if (activeCompetition != null && activeCompetition.isActive()) {
            activeCompetition.end(null);
        }
    }

    private void startInterval() {
        new BukkitRunnable() {
            @Override
            public void run() {
                ConfigManager localConfigManager = configManager;
                if (localConfigManager == null) {
                    localConfigManager = (ConfigManager) plugin.getCoreAPI().getManager("ConfigManager");
                    if (localConfigManager == null) {
                        plugin.getLogger().warning("ConfigManager is null in CompetitionManager task. Skipping execution.");
                        return;
                    }
                    configManager = localConfigManager;
                }
                if (activeCompetition == null || !activeCompetition.isActive()) {
                    int onlinePlayers = Bukkit.getOnlinePlayers().size();
                    int minPlayers = localConfigManager.getConfig().getInt("min-players");
                    if (onlinePlayers >= minPlayers) {
                        activeCompetition = new Competition(plugin, new QuestGenerator(plugin).generateQuest());
                        activeCompetition.start();
                    }
                }
            }
        }.runTaskTimer(plugin, 60, 1200);
    }

    public Competition getActiveCompetition() {
        return activeCompetition;
    }
}