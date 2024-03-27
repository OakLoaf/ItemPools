package me.dave.itempools;

import me.dave.itempools.command.ItemPoolsCommand;
import me.dave.itempools.config.GoalProviderConfigManager;
import me.dave.itempools.config.ItemPoolConfigManager;
import me.dave.itempools.pool.ItemPoolManager;
import me.dave.lushlib.LushLib;
import me.dave.lushlib.plugin.SpigotPlugin;

public final class ItemPools extends SpigotPlugin {
    private static ItemPools plugin;

    @Override
    public void onLoad() {
        plugin = this;
        LushLib.getInstance().enable(this);
    }

    @Override
    public void onEnable() {
        registerManager(
            new GoalProviderConfigManager(),
            new ItemPoolManager(),
            new ItemPoolConfigManager()
        );

        registerCommand(new ItemPoolsCommand());
    }

    @Override
    public void onDisable() {
        LushLib.getInstance().disable();
    }

    public static ItemPools getInstance() {
        return plugin;
    }
}
