package com.trenton.microquests.commands;

import com.trenton.coreapi.annotations.CoreCommand;
import com.trenton.coreapi.api.CoreCommandHandler;
import com.trenton.coreapi.util.MessageUtils;
import com.trenton.microquests.MicroQuests;
import com.trenton.microquests.competition.Competition;
import com.trenton.microquests.managers.CompetitionManager;
import com.trenton.microquests.managers.ConfigManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

@CoreCommand(name = "quest")
public class QuestCommand implements CoreCommandHandler {
    private MicroQuests plugin;
    private ConfigManager configManager;
    private Set<UUID> optOut;

    public void init(MicroQuests plugin) {
        this.plugin = plugin;
        this.configManager = (ConfigManager) plugin.getCoreAPI().getManager("ConfigManager");
        if (this.configManager == null) {
            plugin.getLogger().severe("ConfigManager is null in QuestCommand.init");
            return;
        }
        this.optOut = configManager.getOptOut();
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            if (configManager != null) {
                MessageUtils.sendMessage(configManager.getMessages(), sender, "command-player-only");
            }
            return true;
        }

        if (configManager == null) {
            plugin.getLogger().severe("ConfigManager is null in QuestCommand.execute");
            return true;
        }

        if (args.length == 0) {
            MessageUtils.sendMessage(configManager.getMessages(), player, "command-invalid-usage");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "optout":
                if (optOut.contains(player.getUniqueId())) {
                    optOut.remove(player.getUniqueId());
                    MessageUtils.sendMessage(configManager.getMessages(), player, "optout-disabled");
                } else {
                    optOut.add(player.getUniqueId());
                    MessageUtils.sendMessage(configManager.getMessages(), player, "optout-enabled");
                }
                configManager.saveOptOut();
                return true;
            case "status":
                Competition comp = ((CompetitionManager) plugin.getCoreAPI().getManager("CompetitionManager")).getActiveCompetition();
                if (comp == null || !comp.isActive()) {
                    MessageUtils.sendMessage(configManager.getMessages(), player, "status-no-competition");
                } else {
                    long timeLeft = plugin.getConfig().getLong("max-quest-time") -
                            (System.currentTimeMillis() - comp.getStartTime()) / 1000;
                    MessageUtils.sendMessage(configManager.getMessages(), player, "status-active", comp.getQuest().getObjective(), (int) timeLeft);
                }
                return true;
            default:
                MessageUtils.sendMessage(configManager.getMessages(), player, "command-invalid-usage");
                return true;
        }
    }
}