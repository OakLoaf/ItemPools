package org.lushplugins.itempools.hook;

import org.lushplugins.itempools.ItemPools;
import org.lushplugins.itempools.goal.Goal;
import org.lushplugins.itempools.goal.GoalCollection;
import org.lushplugins.itempools.pool.ItemPool;
import org.lushplugins.itempools.pool.ItemPoolManager;
import org.lushplugins.lushlib.hook.Hook;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public class PlaceholderAPIHook extends Hook {
    private PlaceholderExpansion expansion;

    public PlaceholderAPIHook() {
        super(HookId.PLACEHOLDER_API.toString());
    }

    @Override
    protected void onEnable() {
        expansion = new PlaceholderExpansion();
        expansion.register();
    }

    @Override
    protected void onDisable() {
        if (expansion != null) {
            expansion.unregister();
            expansion = null;
        }
    }

    public static class PlaceholderExpansion extends me.clip.placeholderapi.expansion.PlaceholderExpansion {

        public String onPlaceholderRequest(Player player, @NotNull String params) {
            String[] paramArgs = params.split("_");
            if (paramArgs.length < 3) {
                return null;
            }

            String poolName = paramArgs[0];
            String itemRaw = paramArgs[1];
            String[] poolArgs = Arrays.copyOfRange(paramArgs, 2, paramArgs.length);

            ItemPool itemPool = ItemPools.getInstance().getItemPoolManager().getItemPool(poolName);
            if (itemPool == null) {
                return "Invalid ItemPool";
            }

            GoalCollection goalCollection = itemPool.getGoalCollection();
            Goal goal;
            try {
                goal = goalCollection.get(new ItemStack(Material.valueOf(itemRaw.toUpperCase())));
            } catch (IllegalArgumentException e) {
                return "Invalid Goal";
            }

            if (goal == null) {
                return "Invalid Goal";
            }

            switch (poolArgs[0].toLowerCase()) {
                case "current" -> {
                    return String.valueOf(goal.getValue());
                }
                case "remaining" -> {
                    return String.valueOf(goal.getAmountRemaining());
                }
                case "goal" -> {
                    return String.valueOf(goal.getGoal());
                }
            }

            return null;
        }

        public boolean persist() {
            return true;
        }

        public boolean canRegister() {
            return true;
        }

        @NotNull
        public String getIdentifier() {
            return "itempools";
        }

        @NotNull
        public String getAuthor() {
            return ItemPools.getInstance().getDescription().getAuthors().toString();
        }

        @NotNull
        public String getVersion() {
            return ItemPools.getInstance().getDescription().getVersion();
        }
    }
}
