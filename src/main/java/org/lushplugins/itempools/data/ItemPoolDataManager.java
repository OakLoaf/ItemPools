package org.lushplugins.itempools.data;

import org.lushplugins.itempools.ItemPools;
import org.lushplugins.itempools.data.storage.SQLStorage;
import org.lushplugins.itempools.data.storage.Storage;
import org.lushplugins.itempools.data.storage.YmlStorage;
import org.lushplugins.itempools.goal.GoalCollection;
import org.lushplugins.itempools.pool.ItemPool;
import org.lushplugins.lushlib.manager.Manager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ItemPoolDataManager extends Manager {
    private final ExecutorService threads = Executors.newFixedThreadPool(1);
    private Storage storage;

    @Override
    public void onEnable() {
        ItemPools.getInstance().saveDefaultResource("storage.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(ItemPools.getInstance().getDataFolder(), "storage.yml"));

        String storageType = config.getString("type");
        if (storageType == null) {
            ItemPools.getInstance().getLogger().severe("No storage type defined.");
            return;
        }

        switch (storageType.toLowerCase()) {
            case "mysql", "mariadb" -> {
                String host = config.getString("mysql.host");
                int port = config.getInt("mysql.port");
                String databaseName = config.getString("mysql.database-name");
                String username = config.getString("mysql.username");
                String password = config.getString("mysql.password");

                storage = new SQLStorage(host, port, databaseName, username, password);
            }
            case "yml" -> storage = new YmlStorage();
            default -> {
                ItemPools.getInstance().getLogger().severe("Invalid storage type '" + storageType + "' defined");
                return;
            }
        }

        ItemPools.getInstance().getLogger().info("Successfully loaded '" + storageType + "' storage");
    }

    @Override
    public void onDisable() {
        threads.shutdown();
    }

    public CompletableFuture<Void> updateGoalData(String poolId) {
        return loadPoolData(poolId).thenAccept(poolData -> {
            ItemPool itemPool = ItemPools.getInstance().getItemPoolManager().getItemPool(poolId);
            if (itemPool == null) {
                return;
            }

            GoalCollection goalCollection = itemPool.getGoals();
            goalCollection.clear();
            goalCollection.addAll(poolData.goals().values());

            if (!itemPool.hasCompleted() && poolData.completed()) {
                itemPool.complete();
            }
        });
    }

    public CompletableFuture<ItemPoolGoalData> loadPoolData(String poolId) {
        return runAsync(() -> storage.loadPoolData(poolId));
    }

    public CompletableFuture<Void> savePoolData(ItemPool itemPool) {
        return savePoolData(ItemPoolGoalData.from(itemPool));
    }

    public CompletableFuture<Void> savePoolData(ItemPoolGoalData itemPoolData) {
        return runAsync(() -> storage.savePoolData(itemPoolData));
    }

    public CompletableFuture<Void> deletePoolData(String poolId) {
        return runAsync(() -> storage.deletePoolData(poolId));
    }

    private <T> CompletableFuture<T> runAsync(Callable<T> callable) {
        CompletableFuture<T> future = new CompletableFuture<>();
        threads.submit(() -> {
            try {
                future.complete(callable.call());
            } catch (Throwable e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    private CompletableFuture<Void> runAsync(Runnable runnable) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        threads.submit(() -> {
            try {
                runnable.run();
                future.complete(null);
            } catch (Throwable e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });
        return future;
    }
}
