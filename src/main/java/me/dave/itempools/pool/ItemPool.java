package me.dave.itempools.pool;

import me.dave.itempools.goal.Goal;
import me.dave.itempools.goal.GoalCollection;
import me.dave.itempools.goal.GoalItem;
import me.dave.itempools.region.Region;
import org.bukkit.inventory.ItemStack;

public class ItemPool {
    private final Region region;
    private final GoalCollection goals;

    public ItemPool(Region region) {
        this.region = region;
        this.goals = new GoalCollection();
    }

    public ItemPool(Region region, GoalCollection goals) {
        this.region = region;
        this.goals = goals;
    }

    public Region getRegion() {
        return region;
    }

    public GoalCollection getGoalCollection() {
        return goals;
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
