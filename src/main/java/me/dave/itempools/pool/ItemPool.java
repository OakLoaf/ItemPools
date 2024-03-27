package me.dave.itempools.pool;

import me.dave.itempools.util.GoalItem;
import org.bukkit.inventory.ItemStack;

public class ItemPool {
    public GoalCollection goals;

    public ItemPool() {
        this.goals = new GoalCollection();
    }

    public ItemPool(GoalCollection goals) {
        this.goals = goals;
    }

    public int getValue(GoalItem goalItem) {
        return goals.contains(goalItem) ? goals.get(goalItem).getValue() : 0;
    }

    public boolean containsGoal(ItemStack itemStack) {
        return goals.contains(itemStack);
    }

    public int getGoal(GoalItem goalItem) {
        return goals.contains(goalItem) ? goals.get(goalItem).getGoal() : 0;
    }

    public void addGoal(GoalItem goalItem, int goal) {
        addGoal(new Goal(goalItem, goal));
    }

    public void addGoal(Goal goal) {
        goals.add(goal);
    }

    public void removeGoal(Goal goal) {
        removeGoal(goal.getGoalItem());
    }

    public void removeGoal(GoalItem goalItem) {
        goals.remove(goalItem);
    }
}
