package me.dave.itempools.pool;

import org.bukkit.Material;

import java.util.concurrent.ConcurrentHashMap;

public class GoalCollection {
    private final ConcurrentHashMap<Material, Goal> goals;

    public GoalCollection() {
        this.goals = new ConcurrentHashMap<>();
    }

    public GoalCollection(ConcurrentHashMap<Material, Goal> goals) {
        this.goals = goals;
    }

    public Goal getGoal(Material material) {
        return goals.get(material);
    }

    public boolean contains(Material material) {
        return goals.containsKey(material);
    }

    public void add(Goal goal) {
        goals.put(goal.getMaterial(), goal);
    }

    public void remove(Material material) {
        goals.remove(material);
    }

    public void remove(Goal goal) {
        goals.remove(goal.getMaterial());
    }
}
