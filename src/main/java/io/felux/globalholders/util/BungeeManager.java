package io.felux.globalholders.util;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.felux.globalholders.Globalholders;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.event.EventHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
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
                Player sender = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
                if (sender == null) return;
                requestPlaceholder(sender, "lobby", "GH-Request", "player_name");
            }
        }.runTaskTimerAsynchronously(globalholders, 20L * 10, 20L * 10);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
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

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();

        short len = in.readShort();
        byte[] msgbytes = new byte[len];
        in.readFully(msgbytes);

        DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
        try {
            String somedata = msgin.readUTF(); // Read the data in the same way you wrote it
            short somenumber = msgin.readShort();

            if (subChannel.equalsIgnoreCase("GH-Request")) {
                String placeholderFormatted = "%" + somedata + "%";
                String placeholderResult = PlaceholderAPI.setPlaceholders(player, "%" + somedata + "%");
                globalholders.getLogger().info("[" + subChannel + "] Placeholder '" + placeholderFormatted + "' requested from " + player.getName() + ", result is '" + placeholderResult + "'.");

//                String responseData = "UUID;SERVER;PLACEHOLDER;RESULT";
                requestPlaceholder(player, "lobby", "GH-Receive", player.getUniqueId() + ";" + somedata + ";" + placeholderResult);
            } else {
                String[] resultData = somedata.split(";", 3);
                globalholders.getLogger().info("[" + subChannel + "] Placeholder '%" + resultData[1] + "%' received from " + resultData[0] + " with result: '" + resultData[2] + "'.");
                globalholders.getLogger().info(" ");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
