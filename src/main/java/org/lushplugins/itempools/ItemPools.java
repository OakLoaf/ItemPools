package org.lushplugins.itempools;

import org.lushplugins.itempools.command.ItemPoolsCommand;
import org.lushplugins.itempools.config.GoalProviderConfigManager;
import org.lushplugins.itempools.config.ItemPoolConfigManager;
import org.lushplugins.itempools.data.ItemPoolDataManager;
import org.lushplugins.itempools.hook.FancyHologramsHook;
import org.lushplugins.itempools.hook.PlaceholderAPIHook;
import org.lushplugins.itempools.pool.ItemPoolManager;
import org.lushplugins.lushlib.LushLib;
import org.lushplugins.lushlib.hook.Hook;
import org.lushplugins.lushlib.plugin.SpigotPlugin;

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
        addHook("FancyHolograms", () -> registerHook(new FancyHologramsHook()));
        hooks.values().forEach(Hook::enable);

        registerCommand(new ItemPoolsCommand());
    }

    @Override
    public void onDisable() {
        getManager(ItemPoolManager.class).ifPresent(manager -> getManager(ItemPoolDataManager.class)
            .ifPresent(dataManager -> manager.getItemPools().forEach(dataManager::savePoolData)));

        unregisterAllHooks();
        unregisterAllModules();
        LushLib.getInstance().disable();
    }

    public static ItemPools getInstance() {
        return plugin;
    }
}
