package me.dave.itempools.pool;

import me.dave.platyutils.utils.RandomCollection;
import org.bukkit.Material;

import java.util.concurrent.ConcurrentHashMap;

public class RandomGoalCollection extends GoalCollection {
    private final RandomCollection<Material> randomCollection;

    public RandomGoalCollection(ConcurrentHashMap<Material, WeightedGoal> goals) {
        randomCollection = new RandomCollection<>();
        goals.values().forEach(weightedGoal -> randomCollection.add(weightedGoal.getMaterial(), weightedGoal.getWeight()));
    }

    public Goal nextGoal() {
        return getGoal(randomCollection.next());
    }
}
