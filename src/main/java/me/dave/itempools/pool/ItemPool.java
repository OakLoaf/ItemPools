package me.dave.itempools.pool;

import me.dave.itempools.util.SimpleItemMeta;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

public class ItemPool {
    private final ConcurrentHashMap<Material, PoolMaterialData> poolData;

    public ItemPool(ConcurrentHashMap<Material, PoolMaterialData> poolData) {
        this.poolData = poolData;
    }

    public int getValue(Material material) {
        return poolData.containsKey(material) ? poolData.get(material).getCurrentValue() : 0;
    }

    public void setValue(Material material, int value) {
        if (poolData.containsKey(material)) {
            poolData.get(material).setValue(value);
        }
    }

    public void increaseValue(Material material, int value) {
        if (poolData.containsKey(material)) {
            poolData.get(material).increaseValue(value);
        }
    }

    public int getGoal(Material material) {
        return poolData.containsKey(material) ? poolData.get(material).getGoal() : 0;
    }

    public void addGoal(Material material, int goal) {
        addGoal(material, new PoolMaterialData(null, goal));
    }

    public void addGoal(Material material, PoolMaterialData poolData) {
        this.poolData.put(material, poolData);
    }

    public void removeGoal(Material material) {
        poolData.remove(material);
    }

    public static class PoolMaterialData {
        private final SimpleItemMeta itemMeta;
        private final int goal;
        private int value;

        public PoolMaterialData(@Nullable SimpleItemMeta itemMeta, int goal) {
            this.itemMeta = itemMeta;
            this.goal = goal;
            this.value = 0;
        }

        public PoolMaterialData(@Nullable SimpleItemMeta itemMeta, int goal, int value) {
            this.itemMeta = itemMeta;
            this.goal = goal;
            this.value = value;
        }

        public boolean isValidItemMeta(ItemMeta itemMeta) {
            return this.itemMeta == null || this.itemMeta.isSimilar(itemMeta);
        }

        public int getGoal() {
            return goal;
        }

        public int getCurrentValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public void increaseValue(int value) {
            this.value += value;
        }
    }
}
