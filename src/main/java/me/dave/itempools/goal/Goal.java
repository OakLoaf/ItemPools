package me.dave.itempools.goal;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class Goal {
    private final String id;
    private final GoalItem goalItem;
    private int goal;
    private int value;
    private boolean completed;
    private List<String> completionCommands;

    public Goal(@NotNull String id, @NotNull GoalItem goalItem, int goal) {
        this.id = id;
        this.goalItem = goalItem;
        this.goal = goal;
        this.value = 0;
        this.completionCommands = Collections.emptyList();
    }

    public Goal(@NotNull String id, @NotNull GoalItem goalItem, int goal, int value) {
        this.id = id;
        this.goalItem = goalItem;
        this.goal = goal;
        this.value = value;
        this.completed = false;
        this.completionCommands = Collections.emptyList();
    }

    public Goal(@NotNull String id, @NotNull GoalItem goalItem, int goal, int value, boolean completed) {
        this.id = id;
        this.goalItem = goalItem;
        this.goal = goal;
        this.value = value;
        this.completed = completed;
        this.completionCommands = Collections.emptyList();
    }

    public Goal(@NotNull String id, @NotNull GoalItem goalItem, int goal, int value, boolean completed, List<String> completionCommands) {
        this.id = id;
        this.goalItem = goalItem;
        this.goal = goal;
        this.value = value;
        this.completed = completed;
        this.completionCommands = completionCommands;
    }

    public boolean isCompletable() {
        return value >= goal;
    }

    public boolean hasCompleted() {
        return completed;
    }

    public void complete() {
        completed = true;
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

        completionCommands.forEach(command -> {
            try {
                Bukkit.dispatchCommand(console, command);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });

        onComplete();
    }

    public void onComplete() {}

    public String getId() {
        return id;
    }

    public GoalItem getGoalItem() {
        return goalItem;
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

    public int getAmountRemaining() {
        return Math.max(goal - value, 0);
    }

    public List<String> getCompletionCommands() {
        return completionCommands;
    }

    public void setCompletionCommands(List<String> completionCommands) {
        this.completionCommands = completionCommands;
    }
}
