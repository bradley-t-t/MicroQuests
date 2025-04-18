package com.trenton.microquests.competition.quests;

public abstract class Quest {
    private final String objective;
    private final int amount;

    public Quest(String objective, int amount) {
        this.objective = objective;
        this.amount = amount;
    }

    public String getObjective() {
        return objective;
    }

    public int getAmount() {
        return amount;
    }
}