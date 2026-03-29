package org.lushplugins.itempools;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.itempools.command.ItemPoolsCommand;
import org.lushplugins.itempools.config.ConfigManager;
import org.lushplugins.itempools.config.GoalProviderConfigManager;
import org.lushplugins.itempools.config.ItemPoolConfigManager;
import org.lushplugins.itempools.data.ItemPoolDataManager;
import org.lushplugins.itempools.hook.FancyHologramsHook;
import org.lushplugins.itempools.listener.PluginMessageListener;
import org.lushplugins.itempools.placeholder.Placeholders;
import org.lushplugins.itempools.pool.ItemPool;
import org.lushplugins.itempools.pool.ItemPoolManager;
import org.lushplugins.lushlib.utils.plugin.SpigotPlugin;
import org.lushplugins.lushlib.utils.registry.RegistryUtils;
import org.lushplugins.placeholderhandler.PlaceholderHandler;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.exception.CommandErrorException;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.Consumer;

public final class ItemPools extends SpigotPlugin {
    private static ItemPools plugin;

    private ConfigManager configManager;
    private GoalProviderConfigManager goalProviderConfigManager;
    private ItemPoolManager itemPoolManager;
    private ItemPoolDataManager itemPoolDataManager;
    private ItemPoolConfigManager itemPoolConfigManager;

    @Override
    public void onLoad() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager();
        this.configManager.reload();

        this.goalProviderConfigManager = new GoalProviderConfigManager();
        this.goalProviderConfigManager.reload();

        this.itemPoolManager = new ItemPoolManager();
        this.itemPoolManager.reload();

        this.itemPoolDataManager = new ItemPoolDataManager();
        this.itemPoolDataManager.reload();

        this.itemPoolConfigManager = new ItemPoolConfigManager();
        this.itemPoolConfigManager.reload();

        ifPluginPresent("FancyHolograms", () -> new FancyHologramsHook().enable());

        new PluginMessageListener().register();
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        BukkitLamp.builder(this)
            .parameterTypes(parameterTypes -> parameterTypes
                .addParameterType(ItemPool.class, (input, context) -> {
                    return ItemPools.getInstance().getItemPoolManager().getItemPool(input.readString());
                }))
            .parameterValidator(ItemPool.class, (actor, pool, node, lamp) -> {
                if (pool == null) {
                    throw new CommandErrorException("That is not a valid item pool");
                }
            })
            .suggestionProviders(providers -> providers
                .addProvider(ItemPool.class, (context) -> {
                    return ItemPools.getInstance().getItemPoolManager().getItemPoolIds();
                }))
            .build()
            .register(new ItemPoolsCommand());

        PlaceholderHandler.builder(this)
            .registerParameterProvider(ItemPool.class, (type, parameter, context) -> {
                return ItemPools.getInstance().getItemPoolManager().getItemPool(parameter);
            })
            .registerParameterProvider(Material.class, (type, parameter, context) -> {
                return RegistryUtils.parseString(parameter, Registry.MATERIAL);
            })
            .build()
            .register(new Placeholders());
    }

    @Override
    public void onDisable() {
        ConfigManager configManager = getConfigManager();
        if (configManager != null && configManager.shouldSave()) {
            ItemPoolDataManager dataManager = getItemPoolDataManager();
            for (ItemPool pool : getItemPoolManager().getItemPools()) {
                dataManager.savePoolData(pool);
            }
        }

        itemPoolDataManager.shutdown();

        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
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
        return configManager;
    }

    public GoalProviderConfigManager getGoalProviderConfigManager() {
        return goalProviderConfigManager;
    }

    public ItemPoolManager getItemPoolManager() {
        return itemPoolManager;
    }

    public ItemPoolConfigManager getItemPoolConfigManager() {
        return itemPoolConfigManager;
    }

    public ItemPoolDataManager getItemPoolDataManager() {
        return itemPoolDataManager;
    }

    public static ItemPools getInstance() {
        return plugin;
    }
}
