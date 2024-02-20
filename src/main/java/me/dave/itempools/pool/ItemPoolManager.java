package me.dave.itempools.pool;

import me.dave.platyutils.manager.Manager;

import java.util.concurrent.ConcurrentHashMap;

public class ItemPoolManager extends Manager {
    /**
     * Region name to ItemPool
     */
    private ConcurrentHashMap<String, ItemPool> itemPools;

    @Override
    public void onEnable() {
        itemPools = new ConcurrentHashMap<>();
    }

    @Override
    public void onDisable() {
        if (itemPools != null) {
            itemPools.clear();
            itemPools = null;
        }
    }

    public void addItemPool(String regionName, ItemPool itemPool) {
        itemPools.put(regionName, itemPool);
    }

    public void removeItemPool(String regionName) {
        itemPools.remove(regionName);
    }

    public void removeAllItemPools() {
        itemPools.clear();
    }
}
