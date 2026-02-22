package org.lushplugins.itempools.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.itempools.goal.Goal;
import org.lushplugins.itempools.pool.ItemPool;

public class ItemPoolGoalUpdateEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final ItemPool itemPool;
    private final Goal goal;
    private final int oldGoalValue;
    private final int newGoalValue;

    public ItemPoolGoalUpdateEvent(ItemPool itemPool, Goal goal, int oldGoalValue, int newGoalValue) {
        this.itemPool = itemPool;
        this.goal = goal;
        this.oldGoalValue = oldGoalValue;
        this.newGoalValue = newGoalValue;
    }

    public ItemPool getItemPool() {
        return itemPool;
    }

    public Goal getGoal() {
        return goal;
    }

    public int getOldGoalValue() {
        return oldGoalValue;
    }

    public int getNewGoalValue() {
        return newGoalValue;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
