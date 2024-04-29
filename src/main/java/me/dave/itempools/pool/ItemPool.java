package me.dave.itempools.pool;

import com.mojang.datafixers.util.Pair;
import me.dave.itempools.ItemPools;
import me.dave.itempools.config.GoalProviderConfigManager;
import me.dave.itempools.goal.Goal;
import me.dave.itempools.goal.GoalCollection;
import me.dave.itempools.goal.GoalProvider;
import me.dave.itempools.region.Region;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemPool {
    private final String id;
    private final Region region;
    private final List<Pair<String, Integer>> goalProviders;
    private final GoalCollection defaultGoals;
    private final List<String> completionCommands;

    private final GoalCollection goals;
    private boolean completed;

    private ItemPool(@NotNull String id, @NotNull Region region, @Nullable List<Pair<String, Integer>> goalProviders, @Nullable GoalCollection defaultGoals, @NotNull List<String> completionCommands, @NotNull GoalCollection goals, boolean completed) {
        this.id = id;
        this.region = region;
        this.goalProviders = goalProviders;
        this.defaultGoals = defaultGoals;
        this.completionCommands = completionCommands;

        this.goals = goals;
        this.completed = completed;
    }

    public String getId() {
        return id;
    }

    public Region getRegion() {
        return region;
    }

    public GoalCollection getGoalCollection() {
        return goals;
    }

    public void reset() {
        goals.clear();

        Optional<GoalProviderConfigManager> optionalManager = ItemPools.getInstance().getManager(GoalProviderConfigManager.class);
        if (optionalManager.isPresent() && goalProviders != null) {
            GoalProviderConfigManager goalProviderManager = optionalManager.get();
            goalProviders.forEach((providerData) -> {
                String providerName = providerData.getFirst();
                int amount = providerData.getSecond();

                GoalProvider provider = goalProviderManager.getProvider(providerName);
                if (provider != null) {
                    goals.addAll(provider.nextGoals(amount));
                }
            });
        }

        if (defaultGoals != null) {
            goals.addAll(defaultGoals.values());
        }

        completed = false;
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

    public static class Builder {
        private final String id;
        private final Region region;
        private List<Pair<String, Integer>> goalProviders = new ArrayList<>();
        private GoalCollection defaultGoals = new GoalCollection();
        private List<String> completionCommands = new ArrayList<>();

        private GoalCollection goals = new GoalCollection();
        private boolean completed = false;

        public Builder(String id, Region region) {
            this.id = id;
            this.region = region;
        }

        public Builder setGoalProviders(List<Pair<String, Integer>> goalProviders) {
            this.goalProviders = goalProviders;
            return this;
        }

        public Builder setDefaultGoals(GoalCollection goals) {
            this.defaultGoals = goals;
            return this;
        }

        public Builder setCompletionCommands(List<String> commands) {
            this.completionCommands = commands;
            return this;
        }

        public Builder setGoals(GoalCollection goals) {
            this.goals = goals;
            return this;
        }

        public Builder setCompleted(boolean completed) {
            this.completed = completed;
            return this;
        }

        public ItemPool build() {
            return new ItemPool(id, region, goalProviders, defaultGoals, completionCommands, goals, completed);
        }
    }
}
