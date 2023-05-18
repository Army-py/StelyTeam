package fr.army.stelyteam.external.bungeechatconnect;

import java.util.Objects;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import fr.army.stelyteam.external.MessageHandler;
import fr.flowsqy.bungeechatconnect.event.PrepareMessageEvent;

public class ExternalBCCChatListener implements Listener, MessageHandler {

    private final Queue<Boolean> messageQueue;

    public ExternalBCCChatListener() {
        messageQueue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void handleMessage(UUID senderId, boolean isPrefixed) {
        messageQueue.offer(isPrefixed);
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.HIGH)
    private void onChat(PrepareMessageEvent event) {
        final boolean isPrefixed = Objects.requireNonNull(messageQueue.poll());
        if (!isPrefixed) {
            return;
        }
        event.setCancelled(false);
        event.addExtraData(ExternalBCCReceiveListener.IS_PREFIXED_IDENTIFIER, new byte[0]);
    }
}