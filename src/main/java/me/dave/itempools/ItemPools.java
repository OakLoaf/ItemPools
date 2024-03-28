package me.dave.itempools;

import me.dave.itempools.command.ItemPoolsCommand;
import me.dave.itempools.config.GoalProviderConfigManager;
import me.dave.itempools.config.ItemPoolConfigManager;
import me.dave.itempools.data.ItemPoolDataManager;
import me.dave.itempools.hook.PlaceholderAPIHook;
import me.dave.itempools.pool.ItemPoolManager;
import me.dave.lushlib.LushLib;
import me.dave.lushlib.hook.Hook;
import me.dave.lushlib.module.Module;
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
            new ItemPoolDataManager(),
            new ItemPoolConfigManager()
        );

        addHook("PlaceholderAPI", () -> registerHook(new PlaceholderAPIHook()));
        hooks.values().forEach(Hook::enable);

        registerCommand(new ItemPoolsCommand());
    }

    @Override
    public void onDisable() {
        unregisterAllHooks();
        unregisterAllModules();
        LushLib.getInstance().disable();
    }

    public static ItemPools getInstance() {
        return plugin;
    }
}
