package me.dave.itempools.goal;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class GoalCollection implements Iterable<Goal> {
    private final ConcurrentHashMap<GoalItem, Goal> goals;

    public GoalCollection() {
        this.goals = new ConcurrentHashMap<>();
    }

    public Goal get(ItemStack itemStack) {
        GoalItem goalItem = findGoalItem(itemStack);
        if (goalItem != null) {
            return goals.get(goalItem);
        }

        return null;
    }

    public Goal get(GoalItem goalItem) {
        return goals.get(goalItem).clone();
    }

    public Collection<Goal> values() {
        return goals.values();
    }

    public boolean contains(ItemStack itemStack) {
        return findGoalItem(itemStack) != null;
    }

    public boolean contains(GoalItem goalItem) {
        return goals.containsKey(goalItem);
    }

    public void add(Goal goal) {
        add(goal, false);
    }

    public void add(Goal goal, boolean replace) {
        if (replace) {
            goals.put(goal.getGoalItem(), goal);
        } else {
            GoalItem goalItem = goal.getGoalItem();
            if (goals.containsKey(goalItem)) {
                Goal currGoal = goals.get(goalItem);
                currGoal.setGoal(currGoal.getGoal() + goal.getGoal());
                currGoal.setValue(currGoal.getValue() + goal.getValue());
            } else {
                goals.put(goalItem, goal);
            }
        }
    }

    public void addAll(Collection<Goal> goals) {
        goals.forEach(goal -> add(goal, false));
    }

    public void remove(Goal goal) {
        remove(goal.getGoalItem());
    }

    public void remove(GoalItem goalItem) {
        goals.remove(goalItem);
    }

    public void clear() {
        goals.clear();
    }

    @Nullable
    public GoalItem findGoalItem(ItemStack itemStack) {
        GoalItem completeGoalItem = null;

        for (GoalItem goalItem : goals.keySet()) {
            if (goalItem.isValid(itemStack)) {
                if (goals.get(goalItem).hasCompleted()) {
                    completeGoalItem = goalItem;
                } else {
                    return goalItem;
                }
            }
        }

        return completeGoalItem;
    }

    @NotNull
    @Override
    public Iterator<Goal> iterator() {
        return goals.values().iterator();
    }
}
