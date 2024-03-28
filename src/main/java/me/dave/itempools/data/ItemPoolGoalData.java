package me.dave.itempools.data;

import me.dave.itempools.goal.GoalCollection;
import me.dave.itempools.pool.ItemPool;

public record ItemPoolGoalData(String id, GoalCollection goals) {

    public static ItemPoolGoalData from(ItemPool itemPool) {
        return new ItemPoolGoalData(itemPool.getRegion().getName(), itemPool.getGoalCollection());
    }
}
