package com.trenton.microquests.interfaces;

import org.bukkit.plugin.Plugin;

public interface CommandBase {
    void register(Plugin plugin);
    String getCommandName();
}