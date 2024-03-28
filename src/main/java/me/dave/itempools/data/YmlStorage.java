package me.dave.itempools.data;

import me.dave.itempools.ItemPools;
import me.dave.itempools.goal.Goal;
import me.dave.itempools.goal.GoalCollection;
import me.dave.itempools.goal.GoalItem;
import me.dave.itempools.util.YamlUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.enchantedskies.EnchantedStorage.Storage;

import java.io.File;
import java.io.IOException;

public class YmlStorage implements Storage<ItemPoolGoalData, String> {
    private final File dataFile = new File(ItemPools.getInstance().getDataFolder(), "pool-data.yml");

    @Override
    public ItemPoolGoalData load(String poolId) {
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(dataFile);

        ConfigurationSection poolSection = yamlConfiguration.getConfigurationSection("pools." + poolId);
        if (poolSection != null) {
            GoalCollection goals = new GoalCollection();
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

            return new ItemPoolGoalData(poolId, goals);
        } else {
            return null;
        }
    }

    @Override
    public void save(ItemPoolGoalData itemPoolData) {
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(dataFile);

        ConfigurationSection poolSection = yamlConfiguration.createSection("pools." + itemPoolData.id());
        itemPoolData.goals().forEach(goal -> {
            ConfigurationSection goalSection = poolSection.createSection("goals." + goal.getId());

            goalSection.set("current", goal.getValue());
            goalSection.set("goal", goal.getGoal());
        });

        try {
            yamlConfiguration.save(dataFile);
        } catch(IOException err) {
            err.printStackTrace();
        }
    }
}
