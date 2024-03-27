package me.dave.itempools.pool;

import me.dave.itempools.util.SimpleItemMeta;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WeightedGoal extends Goal {
    private final double weight;

    public WeightedGoal(@NotNull Material material, @Nullable SimpleItemMeta itemMeta, int goal, double weight) {
        super(material, itemMeta, goal);
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }
}
