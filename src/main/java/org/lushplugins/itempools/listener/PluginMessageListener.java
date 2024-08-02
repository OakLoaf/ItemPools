package org.lushplugins.itempools.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.itempools.ItemPools;
import org.lushplugins.itempools.goal.Goal;
import org.lushplugins.itempools.goal.GoalCollection;
import org.lushplugins.itempools.pool.ItemPool;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Level;

public class PluginMessageListener implements org.bukkit.plugin.messaging.PluginMessageListener {

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        if (!subChannel.equals("ItemPools")) {
            return;
        }

        short len = in.readShort();
        byte[] msgBytes = new byte[len];
        in.readFully(msgBytes);

        DataInputStream msgIn = new DataInputStream(new ByteArrayInputStream(msgBytes));
        try {
            switch (msgIn.readUTF()) {
                case "IncrementGoal" -> {
                    String poolId = msgIn.readUTF();
                    String goalId = msgIn.readUTF();
                    int increment = msgIn.readShort();

                    ItemPool itemPool = ItemPools.getInstance().getItemPoolManager().getItemPool(poolId);
                    if (itemPool == null) {
                        return;
                    }

                    GoalCollection goals = itemPool.getGoalCollection();
                    if (goals == null) {
                        return;
                    }

                    Goal goal = goals.get(goalId);
                    if (goal != null) {
                        goal.increaseValue(increment);
                        if (goal.isCompletable()) {
                            goal.complete();
                        }
                    }
                }
                case "" -> {}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register() {
        ItemPools.getInstance().getServer().getMessenger().registerIncomingPluginChannel(ItemPools.getInstance(), "BungeeCord", this);
    }
}
