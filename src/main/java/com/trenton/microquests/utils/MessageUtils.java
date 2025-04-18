package com.trenton.microquests.utils;

import com.trenton.microquests.MicroQuests;
import com.trenton.microquests.competition.quests.Quest;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtils {
    public static String formatEnumName(String enumName) {
        String[] parts = enumName.toLowerCase().split("_");
        StringBuilder formatted = new StringBuilder();
        for (String part : parts) {
            if (formatted.length() > 0) formatted.append(" ");
            formatted.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return formatted.toString();
    }

    public static void sendMessage(MicroQuests plugin, CommandSender sender, String key, Object... args) {
        String message = plugin.getConfigManager().getMessages().getString(key, "");
        if (message.isEmpty()) return;
        message = replacePlaceholders(message, args);
        message = ChatColor.translateAlternateColorCodes('&', message);
        sender.sendMessage(message);
    }

    public static void sendActionBar(MicroQuests plugin, Player player, String key, Quest quest, int progress) {
        String message = plugin.getConfigManager().getMessages().getString(key, "");
        if (message.isEmpty()) return;
        message = replacePlaceholders(message, quest, progress);
        message = ChatColor.translateAlternateColorCodes('&', message);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    public static void broadcast(MicroQuests plugin, String key, Object... args) {
        String message = plugin.getConfigManager().getMessages().getString(key, "");
        if (message.isEmpty()) return;
        message = replacePlaceholders(message, args);
        message = ChatColor.translateAlternateColorCodes('&', message);
        plugin.getServer().broadcastMessage(message);
    }

    public static void sendTitle(MicroQuests plugin, String titleKey, String subtitleKey, Object... args) {
        String title = plugin.getConfigManager().getMessages().getString(titleKey, ""); // titleKey, not key.
        String subtitle = plugin.getConfigManager().getMessages().getString(subtitleKey, "");
        if (title.isEmpty() && subtitle.isEmpty()) return;
        title = replacePlaceholders(title, args);
        subtitle = replacePlaceholders(subtitle, args);
        title = ChatColor.translateAlternateColorCodes('&', title);
        subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.sendTitle(title, subtitle, 10, 60, 10); // Fade in: 0.5s, Stay: 3s, Fade out: 0.5s
        }
    }

    private static String replacePlaceholders(String message, Object... args) {
        if (args.length == 0) return message;
        if (args[0] instanceof Quest quest) {
            message = message.replace("{quest}", quest.getObjective())
                    .replace("{amount}", String.valueOf(quest.getAmount()));
            if (args.length > 1 && args[1] instanceof Integer progress) {
                message = message.replace("{progress}", String.valueOf(progress));
            }
            if (args.length > 1 && args[1] instanceof Player player) {
                message = message.replace("{player}", player.getName());
            }
        } else if (args[0] instanceof Player player) {
            message = message.replace("{player}", player.getName());
        }
        if (args.length > 1 && args[1] instanceof Integer time) {
            message = message.replace("{time}", String.valueOf(time));
        }
        return message;
    }
}