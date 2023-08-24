package fr.army.stelyteam.utils.network;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import fr.army.stelyteam.chat.Message;
import fr.army.stelyteam.team.Storage;
import fr.army.stelyteam.utils.network.chat.MessageWriter;
import fr.army.stelyteam.utils.network.order.Order;
import fr.army.stelyteam.utils.network.order.OrderWriter;
import fr.army.stelyteam.utils.network.storage.StorageWriter;
import fr.flowsqy.noqueuepluginmessage.api.NoQueuePluginMessage;

public class NetworkMessageSender {
    
    public void sendStorage(@NotNull Player player, @NotNull String[] serverNames, @NotNull Storage storage, String sourceServer) {
        final OrderWriter orderWriter = new OrderWriter();
        final StorageWriter storageWriter = new StorageWriter();
        final byte[] orderData;
        final byte[] storageData;
        try {
            storageData = storageWriter.write(storage);
            orderData = orderWriter.write(new Order(sourceServer, storageData));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        NoQueuePluginMessage.sendPluginMessage(player, serverNames, ChannelRegistry.STORAGE_CHANNEL.getChannel(), orderData);
    }

    public void sendMessage(@NotNull Player player, @NotNull String[] serverNames, @NotNull String messageFormat,
            @NotNull String sourceServer, @NotNull Set<UUID> recipients) {
        final OrderWriter orderWriter = new OrderWriter();
        final MessageWriter messageWriter = new MessageWriter();
        final byte[] orderData;
        final byte[] messageData;
        try {
            messageData = messageWriter.write(new Message(player.getUniqueId(), messageFormat, recipients));
            orderData = orderWriter.write(new Order(sourceServer, messageData));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        NoQueuePluginMessage.sendPluginMessage(player, serverNames, ChannelRegistry.TEAM_CHAT_CHANNEL.getChannel(), orderData);
    }
}
