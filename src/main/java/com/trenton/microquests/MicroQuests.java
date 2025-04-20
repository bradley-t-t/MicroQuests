package com.trenton.microquests;

import com.trenton.coreapi.api.CoreAPI;
import com.trenton.updater.api.UpdaterImpl;
import com.trenton.updater.api.UpdaterService;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class MicroQuests extends JavaPlugin {
    private UpdaterService updater;
    private CoreAPI coreAPI;

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
        coreAPI = new CoreAPI(this, packageName);
        coreAPI.initialize();
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
        updater = new UpdaterImpl(this, 124181);
        boolean autoUpdate = getConfig().getBoolean("auto_updater.enabled", true);
        updater.checkForUpdates(autoUpdate);
    }

    public void setupMetrics() {
        new Metrics(this, 25514);
    }

    @Override
    public void onDisable() {
        if (coreAPI != null) {
            coreAPI.shutdown();
        }
        if (updater != null) {
            updater.handleUpdateOnShutdown();
        }
    }

    public CoreAPI getCoreAPI() {
        return coreAPI;
    }

    public UpdaterService getUpdater() {
        return updater;
    }
}