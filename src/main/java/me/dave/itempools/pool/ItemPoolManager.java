package me.dave.itempools.pool;

import me.dave.itempools.ItemPools;
import me.dave.itempools.config.ItemPoolConfigManager;
import me.dave.itempools.goal.Goal;
import me.dave.lushlib.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class ItemPoolManager extends Manager {
    /**
     * Region name to ItemPool
     */
    private ConcurrentHashMap<String, ItemPool> itemPools;
    private BukkitTask poolHeartbeat;

    @Override
    public void onEnable() {
        itemPools = new ConcurrentHashMap<>();
        poolHeartbeat = Bukkit.getScheduler().runTaskTimer(ItemPools.getInstance(), () -> ItemPools.getInstance().getManager(ItemPoolConfigManager.class).ifPresent(itemPoolManager -> {
            itemPools.values().forEach(itemPool -> {
                if (itemPool.hasCompleted()) {
                    return;
                }

                for (Goal goal : itemPool.getGoalCollection()) {
                    if (!goal.hasCompleted() && goal.isCompletable()) {
                        goal.complete();
                    }
                }

                if (itemPool.isCompletable()) {
                    itemPool.complete();
                    return;
                }

                Collection<Entity> entities = itemPool.getRegion().getEntities(entity -> entity.getType().equals(EntityType.DROPPED_ITEM));
                entities.forEach(entity -> {
                    if (entity instanceof Item item) {
                        ItemStack itemStack = item.getItemStack();
                        int amount = itemStack.getAmount();
                        Goal goal = itemPool.getGoalCollection().get(itemStack);
                        if (goal == null || goal.hasCompleted()) {
                            return;
                        }

                        int increase;
                        if (goal.getAmountRemaining() > amount) {
                            item.remove();
                            increase = amount;
                        } else {
                            itemStack.setAmount(itemStack.getAmount() - goal.getAmountRemaining());
                            increase = goal.getAmountRemaining();
                        }

                        goal.increaseValue(increase);
                        if (goal.isCompletable()) {
                            goal.complete();
                        }
                    }
                });
            });
        }),1, 5);
    }

    @Override
    public void onDisable() {
        if (poolHeartbeat != null) {
            poolHeartbeat.cancel();
            poolHeartbeat = null;
        }

        if (itemPools != null) {
            itemPools.clear();
            itemPools = null;
        }
    }

    @Nullable
    public ItemPool getItemPool(String name) {
        return itemPools.get(name);
    }

    public Collection<ItemPool> getItemPools() {
        return itemPools.values();
    }

    public void addItemPool(String regionName, ItemPool itemPool) {
        itemPools.put(regionName, itemPool);
    }

    public void removeItemPool(String regionName) {
        itemPools.remove(regionName);
    }

    public void removeAllItemPools() {
        itemPools.clear();
    }
}
