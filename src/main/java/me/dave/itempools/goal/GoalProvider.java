package me.dave.itempools.goal;

import me.dave.lushlib.utils.IntRange;
import me.dave.lushlib.utils.RandomCollection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class GoalProvider {
    private final ConcurrentHashMap<GoalItem, Goal.Builder> goalProviders;
    private final RandomCollection<GoalItem> randomCollection;

    public GoalProvider() {
        this.goalProviders = new ConcurrentHashMap<>();
        this.randomCollection = new RandomCollection<>();
    }

    public Goal get(GoalItem goalItem) {
        return goalProviders.get(goalItem).build();
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

    public void add(Goal.Builder goalBuilder) {
        add(goalBuilder, false);
    }

    public void add(Goal.Builder goalBuilder, boolean replace) {
        if (replace) {
            goalProviders.put(goalBuilder.getGoalItem(), goalBuilder);
        } else {
            GoalItem goalItem = goalBuilder.getGoalItem();
            if (goalProviders.containsKey(goalItem)) {
                Goal.Builder currGoalBuilder = goalProviders.get(goalItem);

                IntRange currRange = currGoalBuilder.getGoalRange();
                IntRange addedRange = goalBuilder.getGoalRange();
                int minSum = currRange.getMin() + addedRange.getMin();
                int maxSum = currRange.getMax() + addedRange.getMax();

                currGoalBuilder.setGoalRange(new IntRange(minSum, maxSum));
            } else {
                goalProviders.put(goalItem, goalBuilder);
            }
        }

        randomCollection.add(goalBuilder.getGoalItem(), goalBuilder.getWeight());
    }

    public void addAll(Collection<Goal.Builder> goalBuilders) {
        goalBuilders.forEach(goalBuilder -> add(goalBuilder, false));
    }

    public void remove(Goal goal) {
        remove(goal.getGoalItem());
    }

    public void remove(GoalItem goalItem) {
        goalProviders.remove(goalItem);
    }

    public void clear() {
        goalProviders.clear();
    }
}
