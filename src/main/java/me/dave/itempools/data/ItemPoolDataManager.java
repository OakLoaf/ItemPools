package me.dave.itempools.data;

import me.dave.itempools.ItemPools;
import me.dave.itempools.data.storage.SQLStorage;
import me.dave.itempools.data.storage.Storage;
import me.dave.itempools.data.storage.YmlStorage;
import me.dave.lushlib.manager.Manager;
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
                String host = config.getString("host");
                int port = config.getInt("port");
                String databaseName = config.getString("database-name");
                String username = config.getString("username");
                String password = config.getString("password");

                storage = new SQLStorage(host, port, databaseName, username, password);
            }
            case "yml" -> storage = new YmlStorage();
            default -> {
                ItemPools.getInstance().getLogger().severe("Invalid storage type '" + storageType + "' defined.");
                return;
            }
        }

        ItemPools.getInstance().getLogger().severe("Successfully loaded '" + storageType + "' defined.");
    }

    public CompletableFuture<ItemPoolGoalData> loadPoolData(String poolId) {
        return runAsync(() -> storage.loadPoolData(poolId));
    }

    public CompletableFuture<Void> savePoolData(ItemPoolGoalData itemPoolData) {
        return runAsync(() -> storage.savePoolData(itemPoolData));
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
