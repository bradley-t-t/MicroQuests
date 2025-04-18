package com.trenton.microquests;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;

public class MetricsHandler {

    private final MicroQuests plugin;
    private final int pluginId = 25514; // Your bStats plugin ID

    public MetricsHandler(MicroQuests plugin) {
        this.plugin = plugin;
        initializeMetrics();
    }

    private void initializeMetrics() {
        Metrics metrics = new Metrics(plugin, pluginId);

        // Add a custom chart for enabled quest types
        metrics.addCustomChart(new SimplePie("enabled_quest_types", () -> {
            StringBuilder questTypes = new StringBuilder();
            if (plugin.getConfig().getBoolean("quest-types.kill", false)) {
                questTypes.append("Kill,");
            }
            if (plugin.getConfig().getBoolean("quest-types.gather", false)) {
                questTypes.append("Gather,");
            }
            if (plugin.getConfig().getBoolean("quest-types.craft", false)) {
                questTypes.append("Craft,");
            }
            return questTypes.length() > 0 ? questTypes.substring(0, questTypes.length() - 1) : "None";
        }));

        plugin.getLogger().info("bStats metrics enabled with plugin ID: " + pluginId);
    }
}