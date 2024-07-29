package org.lushplugins.itempools;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.itempools.command.ItemPoolsCommand;
import org.lushplugins.itempools.config.ConfigManager;
import org.lushplugins.itempools.config.GoalProviderConfigManager;
import org.lushplugins.itempools.config.ItemPoolConfigManager;
import org.lushplugins.itempools.data.ItemPoolDataManager;
import org.lushplugins.itempools.hook.FancyHologramsHook;
import org.lushplugins.itempools.hook.PlaceholderAPIHook;
import org.lushplugins.itempools.listener.PluginMessageListener;
import org.lushplugins.itempools.pool.ItemPool;
import org.lushplugins.itempools.pool.ItemPoolManager;
import org.lushplugins.lushlib.LushLib;
import org.lushplugins.lushlib.hook.Hook;
import org.lushplugins.lushlib.manager.Manager;
import org.lushplugins.lushlib.plugin.SpigotPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.Consumer;

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
            new ConfigManager(),
            new GoalProviderConfigManager(),
            new ItemPoolManager(),
            new ItemPoolDataManager(),
            new ItemPoolConfigManager()
        );

        addHook("PlaceholderAPI", () -> registerHook(new PlaceholderAPIHook()));
        addHook("FancyHolograms", () -> registerHook(new FancyHologramsHook()));
        hooks.values().forEach(Hook::enable);

        new PluginMessageListener().register();

        registerCommand(new ItemPoolsCommand());
    }

    @Override
    public void onDisable() {
        ItemPoolDataManager dataManager = getItemPoolDataManager();
        for (ItemPool pool : getItemPoolManager().getItemPools()) {
            dataManager.savePoolData(pool);
        }

        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);

        unregisterAllHooks();
        unregisterAllModules();
        LushLib.getInstance().disable();
    }

    public void sendGoalIncrementPluginMessage(String poolId, String goalId, int increment) {
        sendItemPoolsPluginMessage("IncrementGoal", msgOut -> {
            try {
                msgOut.writeUTF(poolId);
                msgOut.writeUTF(goalId);
                msgOut.writeShort(increment);
            } catch (IOException e){
                e.printStackTrace();
            }
        });
    }

    private void sendItemPoolsPluginMessage(@NotNull String messageType, Consumer<DataOutputStream> msgOutConsumer) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF("ALL");
        out.writeUTF("ItemPools");

        ByteArrayOutputStream msgBytes = new ByteArrayOutputStream();
        DataOutputStream msgOut = new DataOutputStream(msgBytes);
        try {
            msgOut.writeUTF(messageType);
            msgOutConsumer.accept(msgOut);
        } catch (IOException exception){
            exception.printStackTrace();
        }

        out.writeShort(msgBytes.toByteArray().length);
        out.write(msgBytes.toByteArray());

        Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        player.sendPluginMessage(ItemPools.getInstance(), "BungeeCord", out.toByteArray());
    }

    public ConfigManager getConfigManager() {
        return getNullableManager(ConfigManager.class);
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
