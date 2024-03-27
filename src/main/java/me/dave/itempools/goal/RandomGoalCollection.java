package me.dave.itempools.goal;

import me.dave.platyutils.utils.RandomCollection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RandomGoalCollection extends GoalCollection {
    private final RandomCollection<GoalItem> randomCollection;

    public RandomGoalCollection() {
        randomCollection = new RandomCollection<>();
    }

    @Override
    public void add(Goal goal) {
        super.add(goal);
        if (goal instanceof WeightedGoal weightedGoal) {
            randomCollection.add(weightedGoal.getGoalItem(), weightedGoal.getWeight());
        }
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
        return get(randomCollection.next());
    }
}
