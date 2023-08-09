package fr.army.stelyteam.utils.network;

import java.io.IOException;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import fr.army.stelyteam.team.Storage;
import fr.army.stelyteam.utils.network.message.OrderWriter;
import fr.flowsqy.noqueuepluginmessage.api.NoQueuePluginMessage;

public class NetworkMessageSender {
    
    public void sendStorage(@NotNull Player player, @NotNull String[] serverNames, @NotNull Storage storage, boolean isOpenHere) {
        final OrderWriter writer = new OrderWriter();
        final byte[] storageData;
        try {
            storage.setOpened(isOpenHere);
            storageData = writer.write(storage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        NoQueuePluginMessage.sendPluginMessage(player, serverNames, ChannelRegistry.STORAGE_CHANNEL.getChannel(), storageData);
    }
}
