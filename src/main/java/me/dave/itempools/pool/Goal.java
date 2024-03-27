package me.dave.itempools.pool;

import me.dave.itempools.util.SimpleItemMeta;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Goal {
    private final Material material;
    private final SimpleItemMeta itemMeta;
    private final int goal;
    private int value;

    public Goal(@NotNull Material material, @Nullable SimpleItemMeta itemMeta, int goal) {
        this.material = material;
        this.itemMeta = itemMeta;
        this.goal = goal;
        this.value = 0;
    }

    public Goal(@NotNull Material material, @Nullable SimpleItemMeta itemMeta, int goal, int value) {
        this.material = material;
        this.itemMeta = itemMeta;
        this.goal = goal;
        this.value = value;
    }

    public Material getMaterial() {
        return material;
    }

    public boolean isValid(ItemStack item) {
        if (item.getType().equals(material)) {
            ItemMeta itemMeta = item.getItemMeta();
            return this.itemMeta == null || itemMeta != null && this.itemMeta.isSimilar(itemMeta);
        }

        return false;
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
