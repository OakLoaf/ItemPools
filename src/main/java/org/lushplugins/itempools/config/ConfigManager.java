package org.lushplugins.itempools.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;
import org.lushplugins.itempools.ItemPools;
import org.lushplugins.itempools.pool.ItemPool;
import org.lushplugins.lushlib.manager.Manager;

public class ConfigManager extends Manager {
    private BukkitTask saveTask;

    public ConfigManager() {
        ItemPools.getInstance().saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        ItemPools plugin = ItemPools.getInstance();

        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        int saveRate = config.getInt("save-rate", -1);
        if (saveRate > 0) {
            long saveRateInTicks = saveRate * 120L;
            saveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
                for (ItemPool pool : plugin.getItemPoolManager().getItemPools()) {
                    plugin.getItemPoolDataManager().savePoolData(pool);
                }
            }, saveRateInTicks, saveRateInTicks);
        }
    }

    @Override
    public void onDisable() {
        if (saveTask != null) {
            saveTask.cancel();
            saveTask = null;
        }
    }

    public void reload() {
        disable();
        enable();
    }
}
