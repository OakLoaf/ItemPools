package org.lushplugins.itempools.goal;

import org.lushplugins.lushlib.utils.IntRange;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Goal implements Cloneable {
    private final String id;
    private final String displayName;
    private final GoalItem goalItem;
    private int goal;
    private int value;
    private boolean completed;
    private List<String> completionCommands;

    protected Goal(@NotNull String id, @Nullable String displayName, @NotNull GoalItem goalItem, int goal, int value, boolean completed, List<String> completionCommands) {
        this.id = id;
        this.displayName = displayName;
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

    public String getDisplayName() {
        return displayName != null ? displayName : id;
    }

    public @NotNull GoalItem getGoalItem() {
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

    @Override
    public Goal clone() {
        return new Goal(
            this.id,
            this.displayName,
            this.goalItem.clone(),
            this.goal,
            this.value,
            this.completed,
            new ArrayList<>(this.completionCommands));
    }

    public static class Builder {
        private final String id;
        private String displayName;
        private GoalItem goalItem;
        private IntRange goalRange;
        private int value;
        private boolean completed;
        private List<String> completionCommands = new ArrayList<>();
        private double weight;

        public Builder(String id) {
            this.id = id;
        }

        public Builder setDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public GoalItem getGoalItem() {
            return goalItem;
        }

        public Builder setGoalItem(GoalItem goalItem) {
            this.goalItem = goalItem;
            return this;
        }

        public Builder setGoal(int goal) {
            return setGoalRange(new IntRange(goal, goal));
        }

        public IntRange getGoalRange() {
            return goalRange;
        }

        public Builder setGoalRange(IntRange goalRange) {
            this.goalRange = goalRange;
            return this;
        }

        public Builder setValue(int value) {
            this.value = value;
            return this;
        }

        public Builder setCompleted(boolean completed) {
            this.completed = completed;
            return this;
        }

        public Builder setCompletionCommands(List<String> completionCommands) {
            this.completionCommands = completionCommands;
            return this;
        }

        public double getWeight() {
            return weight;
        }

        public Builder setWeight(double weight) {
            this.weight = weight;
            return this;
        }

        public Goal build() {
            return new Goal(id, displayName, goalItem, goalRange.next(), value, completed, completionCommands);
        }
    }
}
