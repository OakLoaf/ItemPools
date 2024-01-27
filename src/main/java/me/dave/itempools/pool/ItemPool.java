package me.dave.itempools.pool;

import org.bukkit.Material;

import java.util.concurrent.ConcurrentHashMap;

public class ItemPool {
    private final ConcurrentHashMap<Material, Integer> goals;
    private final ConcurrentHashMap<Material, Integer> values = new ConcurrentHashMap<>();
    
    public ItemPool(ConcurrentHashMap<Material, Integer> goals) {
        this.goals = goals;
    }

    public int getValue(Material material) {
        return values.getOrDefault(material, 0);
    }

    public void setValue(Material material, int value) {
        if (goals.containsKey(material)) {
            values.put(material, value);
        }
    }

    public void increaseValue(Material material, int value) {
        if (goals.containsKey(material)) {
            values.put(material, values.get(material) + value);
        }
    }

    public int getGoal(Material material) {
        return goals.getOrDefault(material, 0);
    }

    public void addGoal(Material material, int goal) {
        goals.put(material, goal);
    }

    public void removeGoal(Material material) {
        goals.remove(material);
    }
}
