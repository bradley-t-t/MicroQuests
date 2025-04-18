package com.trenton.microquests.commands;

import com.trenton.microquests.MicroQuests;
import com.trenton.microquests.competition.Competition;
import com.trenton.microquests.interfaces.CommandBase;
import com.trenton.microquests.managers.ConfigManager;
import com.trenton.microquests.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.UUID;

public class QuestCommand implements CommandBase, CommandExecutor {
    private MicroQuests plugin;
    private ConfigManager configManager;
    private Set<UUID> optOut;

    @Override
    public void register(Plugin plugin) {
        this.plugin = (MicroQuests) plugin;
        this.configManager = this.plugin.getConfigManager();
        this.optOut = configManager.getOptOut();
        PluginCommand cmd = Bukkit.getPluginCommand("quest");
        if (cmd != null) cmd.setExecutor(this);
    }

    @Override
    public String getCommandName() {
        return "quest";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtils.sendMessage(plugin, sender, "command-player-only");
            return true;
        }

        if (args.length == 0) {
            MessageUtils.sendMessage(plugin, player, "command-invalid-usage");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "optout":
                if (optOut.contains(player.getUniqueId())) {
                    optOut.remove(player.getUniqueId());
                    MessageUtils.sendMessage(plugin, player, "optout-disabled");
                } else {
                    optOut.add(player.getUniqueId());
                    MessageUtils.sendMessage(plugin, player, "optout-enabled");
                }
                configManager.saveOptOut();
                return true;
            case "status":
                Competition comp = plugin.getCompetitionManager().getActiveCompetition();
                if (comp == null || !comp.isActive()) {
                    MessageUtils.sendMessage(plugin, player, "status-no-competition");
                } else {
                    long timeLeft = plugin.getConfig().getLong("max-quest-time") -
                            (System.currentTimeMillis() - comp.getStartTime()) / 1000;
                    MessageUtils.sendMessage(plugin, player, "status-active", comp.getQuest(), (int) timeLeft);
                }
                return true;
            default:
                MessageUtils.sendMessage(plugin, player, "command-invalid-usage");
                return true;
        }
    }
}