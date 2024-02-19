package me.dave.itempools.pool;

import me.dave.itempools.ItemPools;
import me.dave.itempools.util.SimpleItemMeta;
import me.dave.platyutils.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class PoolManager {
    private final HashMap<String, ItemPool> regionToItemPool = new HashMap<>();

    public PoolManager() {
        ItemPools.getInstance().saveDefaultResource("item-pools.yml");
    }

    public void reloadConfig() {
        regionToItemPool.clear();

        FileConfiguration regionsConfig = ItemPools.getInstance().getConfigResource("item-pools.yml");
        ConfigurationSection regionsSection = regionsConfig.getConfigurationSection("regions");
        if (regionsSection != null) {
            getConfigurationSections(regionsSection).forEach(regionSection -> {
                ConcurrentHashMap<Material, ItemPool.PoolMaterialData> poolData = new ConcurrentHashMap<>();

                ConfigurationSection itemsSection = regionsSection.getConfigurationSection("items");
                if (itemsSection != null) {
                    getConfigurationSections(itemsSection).forEach(dataSection -> {
                        Material material = StringUtils.getEnum(dataSection.getName(), Material.class).orElse(null);
                        if (material == null) {
                            return;
                        }

                        SimpleItemMeta itemMeta = dataSection.contains("meta") ? SimpleItemMeta.create(dataSection.getConfigurationSection("meta")) : null;
                        int goal = dataSection.getInt("goal");
                        int value = dataSection.getInt("current");

                        poolData.put(material, new ItemPool.PoolMaterialData(itemMeta, goal, value));
                    });
                }

                ItemPool itemPool = new ItemPool(poolData);
                regionToItemPool.put(regionsSection.getName(), itemPool);
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
