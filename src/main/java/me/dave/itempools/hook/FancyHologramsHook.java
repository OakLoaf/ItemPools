package me.dave.itempools.hook;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import me.dave.itempools.ItemPools;
import me.dave.itempools.pool.ItemPoolManager;
import me.dave.lushlib.hook.Hook;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class FancyHologramsHook extends Hook {
    private BukkitTask heartbeat;

    public FancyHologramsHook() {
        super(HookId.FANCY_HOLOGRAMS.toString());
    }

    @Override
    protected void onEnable() {
        updateHolograms();

        heartbeat = Bukkit.getScheduler().runTaskTimerAsynchronously(ItemPools.getInstance(), this::updateHolograms, 20, 20);
    }

    @Override
    protected void onDisable() {
        if (heartbeat != null) {
            heartbeat.cancel();
            heartbeat = null;
        }
    }

    private void updateHolograms() {
        HologramManager hologramManager = FancyHologramsPlugin.get().getHologramManager();
        ItemPools.getInstance().getManager(ItemPoolManager.class).ifPresent(poolManager -> poolManager.getItemPools().forEach(itemPool -> hologramManager.getHologram("ip_" + itemPool.getId()).ifPresent(hologram -> {
            HologramData hologramData = hologram.getData();
            if (hologramData.getTypeData() instanceof TextHologramData textData) {
                List<String> text = new ArrayList<>();
                itemPool.getGoalCollection().forEach(goal -> text.add(goal.getDisplayName() + ": " + goal.getValue() + "/" + goal.getGoal()));
                textData.setText(text);
                hologram.refreshHologram(Bukkit.getOnlinePlayers());
            }
        })));
    }
}
