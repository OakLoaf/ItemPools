package me.dave.itempools.config;

import me.dave.itempools.ItemPools;
import me.dave.itempools.goal.RandomGoalCollection;
import me.dave.itempools.goal.WeightedGoal;
import me.dave.itempools.goal.GoalItem;
import me.dave.itempools.util.YamlUtils;
import me.dave.lushlib.manager.Manager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

public class GoalProviderConfigManager extends Manager {
    private ConcurrentHashMap<String, RandomGoalCollection> goalProviders;

    @Override
    public void onEnable() {
        ItemPools.getInstance().saveDefaultResource("goal-provider.yml");
        FileConfiguration providerConfig = ItemPools.getInstance().getConfigResource("goal-provider.yml");
        goalProviders = new ConcurrentHashMap<>();

        ConfigurationSection providersSection = providerConfig.getConfigurationSection("providers");
        if (providersSection != null) {
            YamlUtils.getConfigurationSections(providersSection).forEach(providerSection -> {
                RandomGoalCollection goals = new RandomGoalCollection();
                YamlUtils.getConfigurationSections(providerSection).forEach(dataSection -> {
                    GoalItem goalItem = GoalItem.create(dataSection);
                    if (goalItem == null) {
                        return;
                    }

                    double weight = dataSection.getDouble("weight", 1);
                    int goal = dataSection.getInt("goal");
                    int value = dataSection.getInt("current");

                    goals.add(new WeightedGoal(dataSection.getName(), goalItem, goal, value, weight));
                });

                goalProviders.put(providerSection.getName(), goals);
            });
        }
    }

    @Override
    public void onDisable() {
        if (goalProviders != null) {
            goalProviders.clear();
            goalProviders = null;
        }
    }

    @Nullable
    public RandomGoalCollection getProvider(String name) {
        return goalProviders.get(name);
    }
}
