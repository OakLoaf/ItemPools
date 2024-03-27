package me.dave.itempools.config;

import me.dave.itempools.ItemPools;
import me.dave.itempools.pool.Goal;
import me.dave.itempools.pool.GoalCollection;
import me.dave.itempools.pool.ItemPool;
import me.dave.itempools.pool.ItemPoolManager;
import me.dave.itempools.region.Region;
import me.dave.itempools.util.SimpleItemMeta;
import me.dave.platyutils.PlatyUtils;
import me.dave.platyutils.manager.Manager;
import me.dave.platyutils.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ItemPoolConfigManager extends Manager {
    /**
     * Region name to Region
     */
    private ConcurrentHashMap<String, Region> regions;

    @Override
    public void onEnable() {
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

                String worldName = regionsSection.getString("world", "undefined");
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

                regions.put(regionName, region);

                GoalCollection goals = new GoalCollection();
                ConfigurationSection itemsSection = regionsSection.getConfigurationSection("items");
                if (itemsSection != null) {
                    getConfigurationSections(itemsSection).forEach(dataSection -> {
                        Material material = StringUtils.getEnum(dataSection.getName(), Material.class).orElse(null);
                        if (material == null) {
                            return;
                        }

                        ConfigurationSection metaSection = dataSection.getConfigurationSection("meta");
                        SimpleItemMeta itemMeta = metaSection != null ? SimpleItemMeta.create(metaSection) : null;
                        int goal = dataSection.getInt("goal");
                        int value = dataSection.getInt("current");

                        goals.add(new Goal(material, itemMeta, goal, value));
                    });
                }

                PlatyUtils.getManager(ItemPoolManager.class).ifPresent(manager -> manager.addItemPool(regionName, new ItemPool(goals)));
            });
        }
    }

    @Override
    public void onDisable() {
        if (regions != null) {
            regions.clear();
            regions = null;
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
