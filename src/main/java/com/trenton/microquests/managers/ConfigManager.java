package com.trenton.microquests.managers;

import com.trenton.coreapi.api.ManagerBase;
import com.trenton.microquests.MicroQuests;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigManager implements ManagerBase {
    private MicroQuests plugin;
    private FileConfiguration config;
    private FileConfiguration messages;
    private FileConfiguration questsConfig;
    private File optOutFile;
    private FileConfiguration optOutConfig;
    private Set<UUID> optOut;

    private List<EntityType> validKillMobs;
    private List<Material> validGatherItems;
    private List<Material> validCraftItems;

    @Override
    public void init(Plugin plugin) {
        this.plugin = (MicroQuests) plugin;
        this.config = plugin.getConfig();
        this.messages = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml"));
        this.questsConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "quests.yml"));
        this.optOutFile = new File(plugin.getDataFolder(), "optout.yml");
        this.optOutConfig = YamlConfiguration.loadConfiguration(optOutFile);
        this.optOut = new HashSet<>();
        if (optOutConfig.contains("optout")) {
            for (String uuid : optOutConfig.getStringList("optout")) {
                optOut.add(UUID.fromString(uuid));
            }
        }
        validateQuestConfigs();
    }

    @Override
    public void shutdown() {
        saveOptOut();
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getMessages() {
        return messages;
    }

    public FileConfiguration getQuestsConfig() {
        return questsConfig;
    }

    public Set<UUID> getOptOut() {
        return optOut;
    }

    public void saveOptOut() {
        optOutConfig.set("optout", optOut.stream().map(UUID::toString).toList());
        try {
            optOutConfig.save(optOutFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save optout.yml: " + e.getMessage());
        }
    }

    private void validateQuestConfigs() {
        validateMobs();
        validateGatherItems();
        validateCraftItems();
    }

    private void validateMobs() {
        List<String> mobs = questsConfig.getStringList("kill_quests.mobs");
        validKillMobs = new ArrayList<>();
        for (int i = 0; i < mobs.size(); i++) {
            String mobName = mobs.get(i);
            try {
                EntityType mob = EntityType.valueOf(mobName.toUpperCase());
                validKillMobs.add(mob);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid mob type in quests.yml: " + mobName + ". Replacing with INVALID_TYPE.");
                mobs.set(i, "INVALID_TYPE");
            }
        }
        questsConfig.set("kill_quests.mobs", mobs);
    }

    private void validateGatherItems() {
        List<String> items = questsConfig.getStringList("gather_quests.items");
        validGatherItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            String itemName = items.get(i);
            try {
                Material item = Material.valueOf(itemName.toUpperCase());
                validGatherItems.add(item);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid item type in quests.yml (gather): " + itemName + ". Replacing with INVALID_TYPE.");
                items.set(i, "INVALID_TYPE");
            }
        }
        questsConfig.set("gather_quests.items", items);
    }

    private void validateCraftItems() {
        List<String> items = questsConfig.getStringList("craft_quests.items");
        validCraftItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            String itemName = items.get(i);
            try {
                Material item = Material.valueOf(itemName.toUpperCase());
                validCraftItems.add(item);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid item type in quests.yml (craft): " + itemName + ". Replacing with INVALID_TYPE.");
                items.set(i, "INVALID_TYPE");
            }
        }
        questsConfig.set("craft_quests.items", items);
    }

    public List<EntityType> getValidKillMobs() {
        return validKillMobs;
    }

    public List<Material> getValidGatherItems() {
        return validGatherItems;
    }

    public List<Material> getValidCraftItems() {
        return validCraftItems;
    }

    public int getKillMinAmount() {
        return questsConfig.getInt("kill_quests.min_amount", 3);
    }

    public int getKillMaxAmount() {
        return questsConfig.getInt("kill_quests.max_amount", 7);
    }

    public int getGatherMinAmount() {
        return questsConfig.getInt("gather_quests.min_amount", 5);
    }

    public int getGatherMaxAmount() {
        return questsConfig.getInt("gather_quests.max_amount", 10);
    }

    public int getCraftMinAmount() {
        return questsConfig.getInt("craft_quests.min_amount", 1);
    }

    public int getCraftMaxAmount() {
        return questsConfig.getInt("craft_quests.max_amount", 5);
    }
}