package me.dave.itempools;

import me.dave.itempools.pool.PoolManager;
import me.dave.platyutils.PlatyUtils;
import me.dave.platyutils.plugin.SpigotPlugin;

public final class ItemPools extends SpigotPlugin {
    private static ItemPools plugin;

    @Override
    public void onLoad() {
        plugin = this;
        PlatyUtils.enable(this);
    }

    @Override
    public void onEnable() {
        PoolManager poolManager = new PoolManager();
        poolManager.reloadConfig();
    }

    @Override
    public void onDisable() {
        PlatyUtils.disable();
    }

    public static ItemPools getInstance() {
        return plugin;
    }
}
