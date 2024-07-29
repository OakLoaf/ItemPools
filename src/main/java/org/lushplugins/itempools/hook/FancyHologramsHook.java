package org.lushplugins.itempools.hook;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.TextHologramData;
import org.lushplugins.itempools.ItemPools;
import org.lushplugins.lushlib.hook.Hook;
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
        ItemPools.getInstance().getItemPoolManager().getItemPools().forEach(
            itemPool -> hologramManager.getHologram("itempool_" + itemPool.getId()).ifPresent(hologram -> {
            if (hologram.getData() instanceof TextHologramData textData) {
                List<String> text = new ArrayList<>();
                itemPool.getGoalCollection().forEach(goal -> text.add(goal.getDisplayName() + ": " + goal.getValue() + "/" + goal.getGoal()));
                textData.setText(text);
            }
        }));
    }
}
