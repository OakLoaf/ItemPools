package me.dave.itempools.pool;

import org.bukkit.Material;

public class ItemPool {
    public GoalCollection goals;

    public ItemPool() {
        this.goals = new GoalCollection();
    }

    public ItemPool(GoalCollection goals) {
        this.goals = goals;
    }

    public int getValue(Material material) {
        return goals.contains(material) ? goals.getGoal(material).getCurrentValue() : 0;
    }

    public void setValue(Material material, int value) {
        if (goals.contains(material)) {
            goals.getGoal(material).setValue(value);
        }
    }

    public void increaseValue(Material material, int value) {
        if (goals.contains(material)) {
            goals.getGoal(material).increaseValue(value);
        }
    }

    public int getGoal(Material material) {
        return goals.contains(material) ? goals.getGoal(material).getGoal() : 0;
    }

    public void addGoal(Material material, int goal) {
        addGoal(new Goal(material, null, goal));
    }

    public void addGoal(Goal goal) {
        goals.add(goal);
    }

    public void removeGoal(Goal goal) {
        removeGoal(goal.getMaterial());
    }

    public void removeGoal(Material material) {
        goals.remove(material);
    }
}
