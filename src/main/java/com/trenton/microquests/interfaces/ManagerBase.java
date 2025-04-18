package com.trenton.microquests.interfaces;

import org.bukkit.plugin.Plugin;

public interface ManagerBase {
    void init(Plugin plugin);
    void shutdown();
}