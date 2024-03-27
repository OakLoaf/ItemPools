package me.dave.itempools.region;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;

public class Region {
    private final String name;
    private String worldName;
    private Location pos1;
    private Location pos2;

    public Region(String name, World world, Location pos1, Location pos2) {
        this.name = name;
        this.worldName = world.getName();
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    /**
     * @return Name of the region
     */
    public String getName() {
        return name;
    }

    /**
     * @return Name of the World that the region is located in
     */
    public String getWorldName() {
        return worldName;
    }

    public void setWorld(World world) {
        this.worldName = world.getName();
    }

    /**
     * @return A copy of the first position
     */
    public Location getPos1() {
        return pos1.clone();
    }

    public void setPos1(Location pos1) {
        this.pos1 = pos1;
    }

    /**
     * @return A copy of the second position
     */
    public Location getPos2() {
        return pos2.clone();
    }

    public void setPos2(Location pos2) {
        this.pos2 = pos2;
    }

    public boolean contains(World world, Location location) {
        if (!world.getName().equalsIgnoreCase(worldName)) {
            return false;
        }

        return BoundingBox.of(pos1, pos2).contains(location.toVector());
    }

    public Collection<Entity> getEntities() {
        return getEntities(null);
    }

    public Collection<Entity> getEntities(Predicate<Entity> filter) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return Collections.emptyList();
        }

        return world.getNearbyEntities(BoundingBox.of(pos1, pos2), filter);
    }
}
