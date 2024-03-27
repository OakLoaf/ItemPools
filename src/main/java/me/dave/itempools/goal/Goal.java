package me.dave.itempools.goal;

import org.jetbrains.annotations.NotNull;

public class Goal {
    private final GoalItem goalItem;
    private int goal;
    private int value;

    public Goal(@NotNull GoalItem goalItem, int goal) {
        this.goalItem = goalItem;
        this.goal = goal;
        this.value = 0;
    }

    public Goal(@NotNull GoalItem goalItem, int goal, int value) {
        this.goalItem = goalItem;
        this.goal = goal;
        this.value = value;
    }

    public GoalItem getGoalItem() {
        return goalItem;
    }

    public boolean isComplete() {
        return value >= goal;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void increaseValue(int value) {
        this.value += value;
    }
}
