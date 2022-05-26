package fr.army.stelyteam.storage.network;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.army.stelyteam.StelyTeamPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayOutputStream;

public class NetworkManager implements PluginMessageListener {

    private final StelyTeamPlugin plugin;
    private boolean load;

    public NetworkManager(StelyTeamPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        synchronized (this) {
            if (load) {
                throw new IllegalStateException("The network manager is already loaded");
            }
            final Messenger messenger = Bukkit.getMessenger();
            messenger.registerIncomingPluginChannel(plugin, "BungeeCord", this);
            messenger.registerOutgoingPluginChannel(plugin, "BungeeCord");
            load = true;
        }
    }

    public void unload() {
        synchronized (this) {
            if (!load) {
                throw new IllegalStateException("The network manager is not loaded");
            }
            final Messenger messenger = Bukkit.getMessenger();
            messenger.unregisterIncomingPluginChannel(plugin, "BungeeCord", this);
            messenger.unregisterOutgoingPluginChannel(plugin, "BungeeCord");
            load = false;
        }
    }

    public void sendMessage(ByteArrayOutputStream msgData) {
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF("ALL");
        out.writeUTF("StelyTeam");

        final byte[] msgBytes = msgData.toByteArray();
        out.writeShort(msgBytes.length);
        out.write(msgBytes);
        Bukkit.getServer().sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        final ByteArrayDataInput in = ByteStreams.newDataInput(message);

        final String subChannel = in.readUTF();
        if (!subChannel.equals("StelyTeam")) {
            return;
        }
        final short msgLength = in.readShort();
        final byte[] msgBytes = new byte[msgLength];
        in.readFully(msgBytes);

        final ByteArrayDataInput msgData = ByteStreams.newDataInput(msgBytes);

        // TODO Handle msgData
    }


}
