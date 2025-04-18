package com.trenton.microquests.competition.quests;

import org.bukkit.Material;

public class GatherQuest extends Quest {
    private final Material item;

    public GatherQuest(Material item, int amount, String objective) {
        super(objective, amount);
        this.item = item;
    }

    public Material getItem() {
        return item;
    }
}