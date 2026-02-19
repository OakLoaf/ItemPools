package org.lushplugins.itempools.placeholder;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.lushplugins.itempools.goal.Goal;
import org.lushplugins.itempools.pool.ItemPool;
import org.lushplugins.placeholderhandler.annotation.Placeholder;
import org.lushplugins.placeholderhandler.annotation.SubPlaceholder;

@Placeholder("itempools")
@SuppressWarnings("unused")
public class Placeholders {

    @SubPlaceholder("<pool>_<goalType>_current")
    public String current(ItemPool pool, Material goalType) {
        Goal goal = pool.getGoals().get(new ItemStack(goalType));
        return goal != null ? String.valueOf(goal.getValue()) : "Invalid Goal";
    }

    @SubPlaceholder("<pool>_<goalType>_remaining")
    public String remaining(ItemPool pool, Material goalType) {
        Goal goal = pool.getGoals().get(new ItemStack(goalType));
        return goal != null ? String.valueOf(goal.getAmountRemaining()) : "Invalid Goal";
    }

    @SubPlaceholder("<pool>_<goalType>_goal")
    public String goal(ItemPool pool, Material goalType) {
        Goal goal = pool.getGoals().get(new ItemStack(goalType));
        return goal != null ? String.valueOf(goal.getGoal()) : "Invalid Goal";
    }
}
