package me.dave.itempools.pool;

import me.dave.itempools.goal.Goal;
import me.dave.itempools.goal.GoalCollection;
import me.dave.itempools.region.Region;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Collections;
import java.util.List;

public class ItemPool {
    private final Region region;
    private final GoalCollection goals;
    private final List<String> completionCommands;
    private boolean completed = false;

    public ItemPool(Region region) {
        this.region = region;
        this.goals = new GoalCollection();
        this.completionCommands = Collections.emptyList();
    }

    public ItemPool(Region region, GoalCollection goals) {
        this.region = region;
        this.goals = goals;
        this.completionCommands = Collections.emptyList();
    }

    public ItemPool(Region region, GoalCollection goals, List<String> completionCommands) {
        this.region = region;
        this.goals = goals;
        this.completionCommands = completionCommands;
    }

    public Region getRegion() {
        return region;
    }

    public GoalCollection getGoalCollection() {
        return goals;
    }

    public boolean isCompletable() {
        for (Goal goal : goals) {
            if (!goal.hasCompleted()) {
                return false;
            }
        }

        return true;
    }

    public boolean hasCompleted() {
        return completed;
    }

    public void complete() {
        completed = true;
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

        completionCommands.forEach(command -> {
            try {
                Bukkit.dispatchCommand(console, command);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });

        onComplete();
    }

    public void onComplete() {}
}
