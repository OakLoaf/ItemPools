package org.lushplugins.itempools.region;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;

public class Region {
    private final String id;
    private final Reference<World> world;
    private final BoundingBox boundingBox;

    public Region(String id, World world, BoundingBox boundingBox) {
        this.id = id;
        this.world = new WeakReference<>(world);
        this.boundingBox = boundingBox;
    }

    public String getId() {
        return id;
    }

    public World getWorld() {
        return world.get();
    }

    public boolean contains(World world, Vector position) {
        if (!this.world.refersTo(world)) {
            return false;
        }

        return this.boundingBox.contains(position);
    }

    public Collection<Entity> getEntities(@Nullable Predicate<Entity> filter) {
        World world = this.world.get();
        return world != null ? world.getNearbyEntities(this.boundingBox, filter) : Collections.emptyList();
    }

    public Collection<Entity> getEntities() {
        return getEntities(null);
    }
}
