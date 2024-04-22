package me.dave.itempools.goal;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WeightedGoal extends Goal {
    private final double weight;

    private WeightedGoal(@NotNull String id, String displayName, @NotNull GoalItem goalItem, int goal, int value, boolean completed, List<String> completionCommands, double weight) {
        super(id, displayName, goalItem, goal, value, completed, completionCommands);
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public static class Builder extends Goal.Builder {
        private double weight;

        public Builder(String id) {
            super(id);
        }

        public Builder setWeight(double weight) {
            this.weight = weight;
            return this;
        }

        @Override
        public WeightedGoal build() {
            return new WeightedGoal(id, displayName, goalItem, goal, value, completed, completionCommands, weight);
        }
    }
}
