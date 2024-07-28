package me.dave.itempools.data;

import me.dave.itempools.data.storage.YmlStorage;
import me.dave.itempools.pool.ItemPool;
import me.dave.lushlib.manager.Manager;
import org.enchantedskies.EnchantedStorage.IOHandler;

import java.util.concurrent.CompletableFuture;

public class ItemPoolDataManager extends Manager {
    private IOHandler<ItemPoolGoalData, String> ioHandler;

    @Override
    public void onEnable() {
        ioHandler = new IOHandler<>(new YmlStorage());
        ioHandler.enable();
    }

    @Override
    public void onDisable() {
        if (ioHandler != null) {
            ioHandler.disable();
            ioHandler = null;
        }
    }

    public CompletableFuture<ItemPoolGoalData> loadItemPoolData(String poolId) {
        return ioHandler.loadData(poolId);
    }

    public void saveItemPoolData(ItemPool itemPool) {
        saveItemPoolData(ItemPoolGoalData.from(itemPool));
    }

    public void saveItemPoolData(ItemPoolGoalData itemPoolData) {
        ioHandler.saveData(itemPoolData);
    }
}
