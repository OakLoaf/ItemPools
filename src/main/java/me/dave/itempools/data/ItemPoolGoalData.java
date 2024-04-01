package me.dave.itempools.data;

import me.dave.itempools.goal.GoalCollection;
import me.dave.itempools.pool.ItemPool;

public record ItemPoolGoalData(String id, GoalCollection goals, boolean completed) {

    public static ItemPoolGoalData from(ItemPool itemPool) {
        return new ItemPoolGoalData(itemPool.getId(), itemPool.getGoalCollection(), itemPool.hasCompleted());
    }
}
