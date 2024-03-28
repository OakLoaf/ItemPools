package me.dave.itempools.pool;

import me.dave.itempools.goal.GoalCollection;
import me.dave.itempools.region.Region;

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
}
