package org.lushplugins.itempools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.lushplugins.itempools.command.ItemPoolsCommand;
import org.lushplugins.itempools.config.GoalProviderConfigManager;
import org.lushplugins.itempools.config.ItemPoolConfigManager;
import org.lushplugins.itempools.data.ItemPoolDataManager;
import org.lushplugins.itempools.hook.FancyHologramsHook;
import org.lushplugins.itempools.hook.PlaceholderAPIHook;
import org.lushplugins.itempools.pool.ItemPoolManager;
import org.lushplugins.lushlib.LushLib;
import org.lushplugins.lushlib.hook.Hook;
import org.lushplugins.lushlib.manager.Manager;
import org.lushplugins.lushlib.plugin.SpigotPlugin;

public final class ItemPools extends SpigotPlugin {
    private static final Gson GSON;
    private static ItemPools plugin;

    static {
        GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    }

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
        getItemPoolManager().getItemPools().forEach(getItemPoolDataManager()::savePoolData);

        unregisterAllHooks();
        unregisterAllModules();
        LushLib.getInstance().disable();
    }

    public GoalProviderConfigManager getGoalProviderConfigManager() {
        return getNullableManager(GoalProviderConfigManager.class);
    }

    public ItemPoolManager getItemPoolManager() {
        return getNullableManager(ItemPoolManager.class);
    }

    public ItemPoolConfigManager getItemPoolConfigManager() {
        return getNullableManager(ItemPoolConfigManager.class);
    }

    public ItemPoolDataManager getItemPoolDataManager() {
        return getNullableManager(ItemPoolDataManager.class);
    }

    private <T extends Manager> T getNullableManager(Class<T> clazz) {
        return getManager(clazz).orElse(null);
    }

    public static Gson getGson() {
        return GSON;
    }

    public static ItemPools getInstance() {
        return plugin;
    }
}
