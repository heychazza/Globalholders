package io.felux.globalholders.util;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.felux.globalholders.Globalholders;
import io.felux.globalholders.api.PlayerCache;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.Map;
import java.util.UUID;

public class BungeeManager implements PluginMessageListener, Listener {

    private Globalholders globalholders;
    private Map<UUID, Map<String, String>> placeholderCache;

    public Map<UUID, Map<String, String>> getPlaceholderCache() {
        return placeholderCache;
    }

    private String serverId = null;

    public BungeeManager(Globalholders globalholders) {
        this.globalholders = globalholders;
        this.placeholderCache = Maps.newHashMap();

        Bukkit.getPluginManager().registerEvents(this, globalholders);

        Bukkit.getMessenger().registerOutgoingPluginChannel(globalholders, "BungeeCord");
        // allow to send to BungeeCord
        Bukkit.getMessenger().registerIncomingPluginChannel(globalholders, "BungeeCord", this);
        // gets a Message from Bungee

        new BukkitRunnable() {
            @Override
            public void run() {
                if (serverId != null) return;
                Player sender = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
                if (sender == null) return;
                getServerId(sender);
            }
        }.runTaskTimerAsynchronously(globalholders, 20L * 10, 20L * 10);
    }

    public String getServerId() {
        return serverId;
    }

    private void getServerId(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServer");
        player.sendPluginMessage(globalholders, "BungeeCord", out.toByteArray());
    }

    public void requestPlaceholder(Player sender, String server, String channel, String placeholder) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);

        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);

        try {
            out.writeUTF("Forward"); // So BungeeCord knows to forward it
            out.writeUTF(server);
            out.writeUTF(channel); // The channel name to check if this your data
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            msgout.writeUTF(placeholder);
            msgout.writeShort(123);
            out.writeShort(msgbytes.toByteArray().length);
            out.write(msgbytes.toByteArray());
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        sender.sendPluginMessage(globalholders, "BungeeCord", stream.toByteArray());
    }

    // TODO: Store the server it was fetched from to send back
    // TODO: Ensure server ID is only stored once.

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();

        if (subChannel.equalsIgnoreCase("GetServer")) {
            this.serverId = in.readUTF();
            return;
        }

        short len = in.readShort();
        byte[] msgbytes = new byte[len];
        in.readFully(msgbytes);

        DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
        try {

            String somedata = msgin.readUTF(); // Read the data in the same way you wrote it
            short somenumber = msgin.readShort();

            if (serverId == null) return;

            if (subChannel.equalsIgnoreCase("RequestPlaceholder")) {
                String placeholderFormatted = "%" + somedata + "%";
                String placeholderResult = PlaceholderAPI.setPlaceholders(player, "%" + somedata + "%");
//                globalholders.getLogger().info("[" + subChannel + "] Placeholder '" + placeholderFormatted + "' requested from " + player.getName() + ", result is '" + placeholderResult + "'.");

                String dataResult = player.getUniqueId() + ";" + serverId + ";" + somedata + ";" + placeholderResult;
                requestPlaceholder(player, serverId, "ReceivePlaceholder", dataResult);
            }

            if (subChannel.equalsIgnoreCase("ReceivePlaceholder")) {
                String[] resultData = somedata.split(";", 4);
//                globalholders.getLogger().info("[" + subChannel + "] Placeholder '%" + resultData[2] + "%' received from server " + resultData[1] + " with result: '" + resultData[3] + "'.");
//                globalholders.getLogger().info(" ");

                UUID playerUuid = UUID.fromString(resultData[0]);
                String serverId = resultData[1];
                String placeholderRaw = "%" + resultData[2] + "%";
                String placeholderResult = resultData[3];

                Map<String, String> placeholderData = Maps.newHashMap();
                placeholderData.put(placeholderRaw, placeholderResult);
                PlayerCache.getPlayer(playerUuid).getPlaceholderCache().put(serverId, placeholderData);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
