package me.dave.itempools.pool;

import me.dave.itempools.util.GoalItem;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class GoalCollection {
    private final ConcurrentHashMap<GoalItem, Goal> goals;

    public GoalCollection() {
        this.goals = new ConcurrentHashMap<>();
    }

    public Goal getGoal(GoalItem goalItem) {
        return goals.get(goalItem);
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
}
