package org.lushplugins.itempools.config;

import com.mojang.datafixers.util.Pair;
import org.lushplugins.itempools.ItemPools;
import org.lushplugins.itempools.data.ItemPoolDataManager;
import org.lushplugins.itempools.goal.Goal;
import org.lushplugins.itempools.goal.GoalItem;
import org.lushplugins.itempools.goal.GoalProvider;
import org.lushplugins.itempools.pool.ItemPool;
import org.lushplugins.itempools.pool.ItemPoolManager;
import org.lushplugins.itempools.region.Region;
import org.lushplugins.itempools.util.YamlUtils;
import org.lushplugins.lushlib.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.lushplugins.itempools.goal.GoalCollection;

import java.util.ArrayList;
import java.util.List;

public class ItemPoolConfigManager extends Manager {

    @Override
    public void onEnable() {
        ItemPools.getInstance().saveDefaultResource("item-pools.yml");
        FileConfiguration regionsConfig = ItemPools.getInstance().getConfigResource("item-pools.yml");

        ItemPoolManager itemPoolManager = ItemPools.getInstance().getManager(ItemPoolManager.class).orElse(null);
        GoalProviderConfigManager providerManager = ItemPools.getInstance().getManager(GoalProviderConfigManager.class).orElse(null);
        if (itemPoolManager == null || providerManager == null) {
            ItemPools.getInstance().getLogger().severe("ItemPoolManager or ProviderManager has not correctly loaded - please report this");
            return;
        }
        itemPoolManager.removeAllItemPools();
        providerManager.reload();

        ConfigurationSection poolsSection = regionsConfig.getConfigurationSection("pools");
        if (poolsSection != null) {
            YamlUtils.getConfigurationSections(poolsSection).forEach(poolSection -> {
                String poolId = poolSection.getName();
                String pos1Raw = poolSection.getString("pos1");
                String pos2Raw = poolSection.getString("pos2");
                if (pos1Raw == null || pos2Raw == null) {
                    ItemPools.getInstance().getLogger().severe("Valid positions aren't determined for region '" + poolId + "'");
                    return;
                }

                String worldName = poolSection.getString("world", "undefined");
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    ItemPools.getInstance().getLogger().severe("An invalid world '" + worldName + "' has been defined for region '" + poolId + "'");
                    return;
                }

                String[] pos1CoordsRaw = pos1Raw.replace(" ", "").split(",");
                String[] pos2CoordsRaw = pos2Raw.replace(" ", "").split(",");

                Region region = new Region(
                    poolId,
                    world,
                    new Location(world, Double.parseDouble(pos1CoordsRaw[0]), Double.parseDouble(pos1CoordsRaw[1]), Double.parseDouble(pos1CoordsRaw[2])),
                    new Location(world, Double.parseDouble(pos2CoordsRaw[0]), Double.parseDouble(pos2CoordsRaw[1]), Double.parseDouble(pos2CoordsRaw[2]))
                );

                GoalCollection goals = new GoalCollection();
                // TODO: Make ImmutableGoal and ImmutableGoalCollection
                GoalCollection defaultGoals = new GoalCollection();
                ConfigurationSection goalsSection = poolSection.getConfigurationSection("goals");
                if (goalsSection != null) {
                    YamlUtils.getConfigurationSections(goalsSection).forEach(goalSection -> {
                        GoalItem goalItem = GoalItem.create(goalSection);
                        if (goalItem == null) {
                            return;
                        }

                        Goal goal = new Goal.Builder(goalSection.getName())
                            .setDisplayName(goalSection.getString("display-name"))
                            .setGoalItem(goalItem)
                            .setGoal(goalSection.getInt("goal"))
                            .setValue(goalSection.getInt("current"))
                            .setCompleted(goalSection.getBoolean("completed"))
                            .setCompletionCommands(goalSection.getStringList("completion-commands"))
                            .build();

                        defaultGoals.add(goal.clone());
                        goals.add(goal);
                    });
                }

                List<Pair<String, Integer>> goalProviders = new ArrayList<>();
                ConfigurationSection providersSection = poolSection.getConfigurationSection("goal-providers");
                if (providersSection != null) {
                    providersSection.getKeys(false).forEach(providerName -> {
                        GoalProvider provider = providerManager.getProvider(providerName);
                        if (provider != null) {
                            int amount = providersSection.getInt(providerName);
                            goalProviders.add(new Pair<>(providerName, amount));
                            goals.addAll(provider.nextGoals(amount));
                        } else {
                            ItemPools.getInstance().getLogger().severe("Provider '" + providerName + "' at '" + providersSection.getCurrentPath() + "' is not a valid provider");
                        }
                    });
                }

                List<String> poolCompletionCommands = poolSection.getStringList("completion-commands");

                ItemPool.Builder builder = new ItemPool.Builder(poolId, region)
                    .setDefaultGoals(defaultGoals)
                    .setGoalProviders(goalProviders)
                    .setGoals(goals)
                    .setCompletionCommands(poolCompletionCommands);

                ItemPools.getInstance().getManager(ItemPoolDataManager.class).ifPresentOrElse(
                    poolDataManager -> poolDataManager.loadPoolData(poolId).thenAccept(itemPoolData -> {
                        try {
                            if (itemPoolData != null) {
                                GoalCollection loadedGoals = itemPoolData.goals();
                                loadedGoals.forEach(goal -> goal.setCompletionCommands(loadedGoals.get(goal.getGoalItem()).getCompletionCommands()));
                                builder
                                    .setGoals(loadedGoals)
                                    .setCompleted(itemPoolData.completed());
                            }

                            itemPoolManager.addItemPool(builder.build());
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }),
                    () -> itemPoolManager.addItemPool(builder.build()));
            });
        }
    }

    public void reload() {
        disable();
        enable();
    }
}
