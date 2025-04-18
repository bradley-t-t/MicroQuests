package com.trenton.microquests;

import com.trenton.coreapi.api.PluginInitializer;
import com.trenton.microquests.managers.CompetitionManager;
import com.trenton.microquests.managers.ConfigManager;
import com.trenton.microquests.managers.RewardManager;
import com.trenton.updater.api.UpdaterImpl;
import com.trenton.updater.api.UpdaterService;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class MicroQuests extends JavaPlugin {
    private ConfigManager configManager;
    private RewardManager rewardManager;
    private CompetitionManager competitionManager;
    private UpdaterService updater;
    private PluginInitializer initializer;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        setupFiles();
        initialize();
        setupUpdater();
        setupMetrics();
    }

    public void initialize() {
        String packageName = getClass().getPackageName();
        initializer = new PluginInitializer(this, packageName);
        initializer.initialize();
    }

    private void setupFiles() {
        File messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
        File questsFile = new File(getDataFolder(), "quests.yml");
        if (!questsFile.exists()) {
            saveResource("quests.yml", false);
        }
    }

    public void setupUpdater() {
        updater = new UpdaterImpl(this, 124181); // SpigotMC resource ID for MicroQuests
        boolean autoUpdate = getConfig().getBoolean("auto_updater.enabled", true);
        updater.checkForUpdates(autoUpdate);
    }

    public void setupMetrics() {
        new Metrics(this, 25514);
    }

    @Override
    public void onDisable() {
        if (initializer != null) {
            initializer.shutdown();
        }
        if (updater != null) {
            updater.handleUpdateOnShutdown();
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public RewardManager getRewardManager() {
        return rewardManager;
    }

    public CompetitionManager getCompetitionManager() {
        return competitionManager;
    }
}