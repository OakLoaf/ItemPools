package me.dave.itempools.config;

import me.dave.itempools.ItemPools;
import me.dave.itempools.goal.Goal;
import me.dave.itempools.goal.GoalCollection;
import me.dave.itempools.goal.RandomGoalCollection;
import me.dave.itempools.pool.*;
import me.dave.itempools.region.Region;
import me.dave.itempools.goal.GoalItem;
import me.dave.lushlib.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ItemPoolConfigManager extends Manager {

    @Override
    public void onEnable() {
        ItemPools.getInstance().saveDefaultResource("item-pools.yml");
        FileConfiguration regionsConfig = ItemPools.getInstance().getConfigResource("item-pools.yml");

        ConfigurationSection regionsSection = regionsConfig.getConfigurationSection("regions");
        if (regionsSection != null) {
            getConfigurationSections(regionsSection).forEach(regionSection -> {
                String regionName = regionSection.getName();
                String pos1Raw = regionSection.getString("pos1");
                String pos2Raw = regionSection.getString("pos2");
                if (pos1Raw == null || pos2Raw == null) {
                    ItemPools.getInstance().getLogger().severe("Valid positions aren't determined for region '" + regionName + "'");
                    return;
                }

                String worldName = regionSection.getString("world", "undefined");
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    ItemPools.getInstance().getLogger().severe("An invalid world '" + worldName +  "' has been defined for region '" + regionName + "'");
                    return;
                }

                String[] pos1CoordsRaw = pos1Raw.replace(" ", "").split(",");
                String[] pos2CoordsRaw = pos2Raw.replace(" ", "").split(",");

                Region region = new Region(
                    regionName,
                    world,
                    new Location(world, Double.parseDouble(pos1CoordsRaw[0]), Double.parseDouble(pos1CoordsRaw[1]), Double.parseDouble(pos1CoordsRaw[2])),
                    new Location(world, Double.parseDouble(pos2CoordsRaw[0]), Double.parseDouble(pos2CoordsRaw[1]), Double.parseDouble(pos2CoordsRaw[2]))
                );

                GoalCollection goals = new GoalCollection();
                ConfigurationSection providersSection = regionSection.getConfigurationSection("goal-providers");
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


                ConfigurationSection itemsSection = regionSection.getConfigurationSection("items");
                if (itemsSection != null) {
                    getConfigurationSections(itemsSection).forEach(dataSection -> {
                        GoalItem goalItem = GoalItem.create(dataSection);
                        if (goalItem == null) {
                            return;
                        }

                        int goal = dataSection.getInt("goal");
                        int value = dataSection.getInt("current");

                        goals.add(new Goal(goalItem, goal, value));
                    });
                }

                ItemPools.getInstance().getManager(ItemPoolManager.class).ifPresent(manager -> manager.addItemPool(regionName, new ItemPool(region, goals)));
            });
        }
    }

    private static List<ConfigurationSection> getConfigurationSections(ConfigurationSection configurationSection) {
        return configurationSection.getValues(false)
            .values()
            .stream()
            .filter(sectionRaw -> sectionRaw instanceof ConfigurationSection)
            .map(sectionRaw -> (ConfigurationSection) sectionRaw)
            .toList();
    }
}
