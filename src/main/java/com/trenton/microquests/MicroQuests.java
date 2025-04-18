package com.trenton.microquests;

import com.trenton.microquests.interfaces.CommandBase;
import com.trenton.microquests.interfaces.ListenerBase;
import com.trenton.microquests.interfaces.ManagerBase;
import com.trenton.microquests.managers.ConfigManager;
import com.trenton.microquests.managers.CompetitionManager;
import com.trenton.microquests.managers.RewardManager;
import com.trenton.microquests.updater.UpdateChecker;
import com.trenton.microquests.utils.ReflectionUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public final class MicroQuests extends JavaPlugin {
    private List<ManagerBase> managers;
    private List<CommandBase> commands;
    private List<ListenerBase> listeners;
    private ConfigManager configManager;
    private RewardManager rewardManager;
    private CompetitionManager competitionManager;
    private UpdateChecker updateChecker;

    @Override
    public void onEnable() {
        // Save default config, messages, and quests only if they don't exist
        saveDefaultConfig();
        File messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
        File questsFile = new File(getDataFolder(), "quests.yml");
        if (!questsFile.exists()) {
            saveResource("quests.yml", false);
        }

        // Initialize components via reflection
        String packageName = getClass().getPackageName();
        managers = ReflectionUtils.initializeClasses(this, packageName, ManagerBase.class);
        commands = ReflectionUtils.initializeClasses(this, packageName, CommandBase.class);
        listeners = ReflectionUtils.initializeClasses(this, packageName, ListenerBase.class);

        // Assign specific managers
        for (ManagerBase manager : managers) {
            if (manager instanceof ConfigManager) {
                configManager = (ConfigManager) manager;
            } else if (manager instanceof RewardManager) {
                rewardManager = (RewardManager) manager;
            } else if (manager instanceof CompetitionManager) {
                competitionManager = (CompetitionManager) manager;
            }
        }

        // Register components
        managers.forEach(m -> m.init(this));
        commands.forEach(c -> c.register(this));
        listeners.forEach(l -> l.register(this));

        updateChecker = new UpdateChecker(this,124181);
        boolean autoUpdate = getConfig().getBoolean("auto_updater.enabled", true);
        updateChecker.checkForUpdates(autoUpdate);

        // Initialize bStats metrics
        new MetricsHandler(this);
    }

    @Override
    public void onDisable() {
        // Shutdown managers
        managers.forEach(ManagerBase::shutdown);
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