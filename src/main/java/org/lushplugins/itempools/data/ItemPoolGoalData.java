package org.lushplugins.itempools.data;

import org.lushplugins.itempools.goal.GoalCollection;
import org.lushplugins.itempools.pool.ItemPool;

public record ItemPoolGoalData(String id, GoalCollection goals, boolean completed) {

    public static ItemPoolGoalData from(ItemPool itemPool) {
        return new ItemPoolGoalData(itemPool.getId(), itemPool.getGoals(), itemPool.hasCompleted());
    }
}
