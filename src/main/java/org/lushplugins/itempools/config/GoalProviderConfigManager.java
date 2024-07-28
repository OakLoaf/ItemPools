package org.lushplugins.itempools.config;

import org.lushplugins.itempools.ItemPools;
import org.lushplugins.itempools.goal.Goal;
import org.lushplugins.itempools.goal.GoalProvider;
import org.lushplugins.itempools.goal.GoalItem;
import org.lushplugins.itempools.util.YamlUtils;
import org.lushplugins.lushlib.manager.Manager;
import org.lushplugins.lushlib.utils.IntRange;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

public class GoalProviderConfigManager extends Manager {
    private ConcurrentHashMap<String, GoalProvider> goalProviders;

    @Override
    public void onEnable() {
        ItemPools.getInstance().saveDefaultResource("goal-provider.yml");
        FileConfiguration providerConfig = ItemPools.getInstance().getConfigResource("goal-provider.yml");
        goalProviders = new ConcurrentHashMap<>();

        ConfigurationSection providersSection = providerConfig.getConfigurationSection("providers");
        if (providersSection != null) {
            YamlUtils.getConfigurationSections(providersSection).forEach(providerSection -> {
                GoalProvider goalProvider = new GoalProvider();
                YamlUtils.getConfigurationSections(providerSection).forEach(dataSection -> {
                    GoalItem goalItem = GoalItem.create(dataSection);
                    if (goalItem == null) {
                        return;
                    }

                    goalProvider.add(new Goal.Builder(dataSection.getName())
                        .setDisplayName(dataSection.getString("display-name"))
                        .setGoalItem(goalItem)
                        .setGoalRange(IntRange.parseIntRange(dataSection.getString("goal", "0")))
                        .setValue(dataSection.getInt("current"))
                        .setCompleted(dataSection.getBoolean("completed", false))
                        .setCompletionCommands(dataSection.getStringList("completion-commands"))
                        .setWeight(dataSection.getDouble("weight", 1))
                    );
                });

                goalProviders.put(providerSection.getName(), goalProvider);
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

    public void reload() {
        disable();
        enable();
    }

    @Nullable
    public GoalProvider getProvider(String name) {
        return goalProviders.get(name);
    }
}
