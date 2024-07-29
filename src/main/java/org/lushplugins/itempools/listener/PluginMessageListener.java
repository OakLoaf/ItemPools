package org.lushplugins.itempools.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.itempools.ItemPools;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class PluginMessageListener implements org.bukkit.plugin.messaging.PluginMessageListener {

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        if (subChannel.equals("ItemPools")) {
            short len = in.readShort();
            byte[] msgBytes = new byte[len];
            in.readFully(msgBytes);

            DataInputStream msgIn = new DataInputStream(new ByteArrayInputStream(msgBytes));
            try {
                String poolId = msgIn.readUTF();
                ItemPools.getInstance().getItemPoolDataManager().updateGoalData(poolId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void register() {
        ItemPools.getInstance().getServer().getMessenger().registerIncomingPluginChannel(ItemPools.getInstance(), "BungeeCord", this);
    }
}
