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
            boolean poolCompleted = poolSection.getBoolean("completed");

            GoalCollection goals = new GoalCollection();
            ConfigurationSection goalsSection = poolSection.getConfigurationSection("goals");
            if (goalsSection != null) {
                YamlUtils.getConfigurationSections(goalsSection).forEach(goalSection -> {
                    GoalItem goalItem = GoalItem.create(goalSection);
                    if (goalItem == null) {
                        return;
                    }

                    goals.add(new Goal.Builder(goalSection.getName())
                        .setDisplayName(goalSection.getString("display-name"))
                        .setGoalItem(goalItem)
                        .setGoal(goalSection.getInt("goal"))
                        .setValue(goalSection.getInt("current"))
                        .setCompleted(goalSection.getBoolean("completed", false))
                        .build()
                    );
                });
            }

            return new ItemPoolGoalData(poolId, goals, poolCompleted);
        } else {
            return null;
        }
    }

    @Override
    public void save(ItemPoolGoalData itemPoolData) {
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(dataFile);

        ConfigurationSection poolSection = yamlConfiguration.createSection("pools." + itemPoolData.id());
        poolSection.set("completed", itemPoolData.completed());
        itemPoolData.goals().forEach(goal -> {
            ConfigurationSection goalSection = poolSection.createSection("goals." + goal.getId());

            if (goal.getDisplayName() != null) {
                goalSection.set("display-name", goal.getDisplayName());
            }
            goalSection.set("current", goal.getValue());
            goalSection.set("goal", goal.getGoal());
            goalSection.set("completed", goal.hasCompleted());
        });

        try {
            yamlConfiguration.save(dataFile);
        } catch(IOException err) {
            err.printStackTrace();
        }
    }
}
