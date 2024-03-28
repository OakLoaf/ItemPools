package me.dave.itempools.goal;

import org.jetbrains.annotations.NotNull;

public class WeightedGoal extends Goal {
    private final double weight;

    public WeightedGoal(@NotNull String id, @NotNull GoalItem goalItem, int goal, double weight) {
        super(id, goalItem, goal);
        this.weight = weight;
    }

    public WeightedGoal(@NotNull String id, @NotNull GoalItem goalItem, int goal, int value, double weight) {
        super(id, goalItem, goal, value);
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }
}
