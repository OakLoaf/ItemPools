package me.dave.itempools.config;

import me.dave.itempools.ItemPools;
import me.dave.itempools.data.ItemPoolDataManager;
import me.dave.itempools.goal.Goal;
import me.dave.itempools.goal.GoalCollection;
import me.dave.itempools.goal.RandomGoalCollection;
import me.dave.itempools.pool.*;
import me.dave.itempools.region.Region;
import me.dave.itempools.goal.GoalItem;
import me.dave.itempools.util.YamlUtils;
import me.dave.lushlib.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class ItemPoolConfigManager extends Manager {

    @Override
    public void onEnable() {
        ItemPools.getInstance().saveDefaultResource("item-pools.yml");
        FileConfiguration regionsConfig = ItemPools.getInstance().getConfigResource("item-pools.yml");

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
                ConfigurationSection providersSection = poolSection.getConfigurationSection("goal-providers");
                if (providersSection != null) {
                    providersSection.getKeys(false).forEach(providerName -> ItemPools.getInstance().getManager(GoalProviderConfigManager.class).ifPresent(providerManager -> {
                        RandomGoalCollection provider = providerManager.getProvider(providerName);
                        if (provider != null) {
                            goals.addAll(provider.nextGoals(providersSection.getInt(providerName)));
                        } else {
                            ItemPools.getInstance().getLogger().severe("Provider '" + providerName + "' at '" + providersSection.getCurrentPath() + "' is not a valid provider");
                        }
                    }));
                }

                ItemPools.getInstance().getManager(ItemPoolDataManager.class).ifPresentOrElse(
                    poolDataManager -> poolDataManager.loadItemPoolData(poolId).thenAccept(itemPoolData -> {
                        ItemPools.getInstance().getManager(ItemPoolManager.class).ifPresent(manager -> manager.addItemPool(poolId, new ItemPool(region, itemPoolData.goals())));
                    }),
                    () -> {
                        ConfigurationSection goalsSection = poolSection.getConfigurationSection("goals");
                        if (goalsSection != null) {
                            YamlUtils.getConfigurationSections(goalsSection).forEach(dataSection -> {
                                GoalItem goalItem = GoalItem.create(dataSection);
                                if (goalItem == null) {
                                    return;
                                }

                                int goal = dataSection.getInt("goal");
                                int value = dataSection.getInt("current");

                                goals.add(new Goal(dataSection.getName(), goalItem, goal, value));
                            });
                        }

                        ItemPools.getInstance().getManager(ItemPoolManager.class).ifPresent(manager -> manager.addItemPool(poolId, new ItemPool(region, goals)));
                    });
            });
        }
    }
}
