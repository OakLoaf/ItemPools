package me.dave.itempools;

import me.dave.itempools.command.ItemPoolsCommand;
import me.dave.itempools.config.GoalProviderConfigManager;
import me.dave.itempools.config.ItemPoolConfigManager;
import me.dave.itempools.pool.ItemPoolManager;
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
        PlatyUtils.registerManager(
            new GoalProviderConfigManager(),
            new ItemPoolManager(),
            new ItemPoolConfigManager()
        );

        registerCommand(new ItemPoolsCommand());
    }

    @Override
    public void onDisable() {
        PlatyUtils.disable();
    }

    public static ItemPools getInstance() {
        return plugin;
    }
}
