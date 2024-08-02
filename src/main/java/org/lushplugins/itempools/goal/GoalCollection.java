package org.lushplugins.itempools.goal;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.itempools.ItemPools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GoalCollection implements Iterable<Goal> {
    private final ConcurrentHashMap<GoalItem, Goal> goals;

    public GoalCollection(Collection<Goal> goals) {
        this.goals = new ConcurrentHashMap<>(goals.stream().collect(Collectors.toMap(Goal::getGoalItem, Function.identity())));
    }

    public GoalCollection() {
        this.goals = new ConcurrentHashMap<>();
    }

    public Goal get(ItemStack itemStack) {
        GoalItem goalItem = findGoalItem(itemStack);
        if (goalItem != null) {
            return goals.get(goalItem);
        }

        return null;
    }

    public Goal get(GoalItem goalItem) {
        return goals.get(goalItem).clone();
    }

    public Goal get(String id) {
        for (Goal goal : goals.values()) {
            if (goal.getId().equals(id)) {
                return goal;
            }
        }

        return null;
    }

    public Collection<Goal> values() {
        return goals.values();
    }

    public boolean contains(ItemStack itemStack) {
        return findGoalItem(itemStack) != null;
    }

    public boolean contains(GoalItem goalItem) {
        return goals.containsKey(goalItem);
    }

    public void add(Goal goal) {
        add(goal, false);
    }

    public void add(Goal goal, boolean replace) {
        if (replace) {
            goals.put(goal.getGoalItem(), goal);
        } else {
            GoalItem goalItem = goal.getGoalItem();
            if (goals.containsKey(goalItem)) {
                Goal currGoal = goals.get(goalItem);
                currGoal.setGoal(currGoal.getGoal() + goal.getGoal());
                currGoal.setValue(currGoal.getValue() + goal.getValue());
            } else {
                goals.put(goalItem, goal);
            }
        }
    }

    public void addAll(Collection<Goal> goals) {
        goals.forEach(goal -> add(goal, false));
    }

    public void remove(Goal goal) {
        remove(goal.getGoalItem());
    }

    public void remove(GoalItem goalItem) {
        goals.remove(goalItem);
    }

    public void clear() {
        goals.clear();
    }

    @Nullable
    public GoalItem findGoalItem(ItemStack itemStack) {
        GoalItem completeGoalItem = null;

        for (GoalItem goalItem : goals.keySet()) {
            if (goalItem.isValid(itemStack)) {
                if (goals.get(goalItem).hasCompleted()) {
                    completeGoalItem = goalItem;
                } else {
                    return goalItem;
                }
            }
        }

        return completeGoalItem;
    }

    @NotNull
    @Override
    public Iterator<Goal> iterator() {
        return goals.values().iterator();
    }

    public JsonElement toJson() {
        JsonArray goalsJson = new JsonArray();

        for (Goal goal : goals.values()) {
            JsonObject goalJson = new JsonObject();

            goalJson.addProperty("goal-id", goal.getId());
            goalJson.add("item", goal.getGoalItem().toJson());
            goalJson.addProperty("display-name", goal.getDisplayName());
            goalJson.addProperty("current", goal.getValue());
            goalJson.addProperty("goal", goal.getGoal());
            goalJson.addProperty("completed", goal.hasCompleted());
            goalJson.addProperty("completion-commands", ItemPools.getGson().toJson(goal.getCompletionCommands()));

            goalsJson.add(goalJson);
        }

        return goalsJson;
    }

    public static GoalCollection fromJson(JsonElement json) {
        JsonArray goalsJson = json.getAsJsonArray();

        List<Goal> goals = new ArrayList<>();
        for (JsonElement goalJsonRaw : goalsJson) {
            JsonObject goalJson = goalJsonRaw.getAsJsonObject();

            Goal goal = new Goal.Builder(goalJson.get("goal-id").getAsString())
                .setGoalItem(GoalItem.fromJson(goalJson.getAsJsonObject("item")))
                .setDisplayName(goalJson.get("display-name").getAsString())
                .setValue(goalJson.get("current").getAsInt())
                .setGoal(goalJson.get("goal").getAsInt())
                .setCompleted(goalJson.get("completed").getAsBoolean())
                .setCompletionCommands(ItemPools.getGson().fromJson(goalJson.get("completion-commands"), new TypeToken<List<String>>(){}.getType()))
                .build();

            goals.add(goal);
        }

        return new GoalCollection(goals);
    }
}
