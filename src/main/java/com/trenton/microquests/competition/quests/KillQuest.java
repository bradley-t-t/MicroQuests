package com.trenton.microquests.competition.quests;

import org.bukkit.entity.EntityType;

public class KillQuest extends Quest {
    private final EntityType mob;

    public KillQuest(EntityType mob, int amount, String objective) {
        super(objective, amount);
        this.mob = mob;
    }

    public EntityType getMob() {
        return mob;
    }
}