package me.dave.itempools.pool;

import me.dave.platyutils.utils.RandomCollection;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class RandomGoalCollection extends GoalCollection {
    private final RandomCollection<Material> randomCollection;

    public RandomGoalCollection(ConcurrentHashMap<Material, WeightedGoal> goals) {
        randomCollection = new RandomCollection<>();
        goals.values().forEach(weightedGoal -> randomCollection.add(weightedGoal.getMaterial(), weightedGoal.getWeight()));
    }

    @NotNull
    public List<Goal> nextGoals(int amount) {
        List<Goal> goals = new ArrayList<>();
        for (int i = 0; i < amount; i ++) {
            goals.add(nextGoal());
        }
        return goals;
    }

    public Goal nextGoal() {
        return getGoal(randomCollection.next());
    }
}
