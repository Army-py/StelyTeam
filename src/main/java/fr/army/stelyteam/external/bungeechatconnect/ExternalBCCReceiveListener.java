package fr.army.stelyteam.external.bungeechatconnect;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import fr.army.stelyteam.external.bungeechatconnect.handler.RecipientsHandler;
import fr.flowsqy.bungeechatconnect.event.BungeePlayerChatEvent;

public class ExternalBCCReceiveListener implements Listener {

    public final static String IS_PREFIXED_IDENTIFIER = "stelychat:teamchat";

    private final RecipientsHandler recipientsHandler;

    public ExternalBCCReceiveListener(RecipientsHandler recipientsHandler) {
        this.recipientsHandler = recipientsHandler;
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.HIGH)
    public void onReceiveMessage(BungeePlayerChatEvent event) {
        final byte[] prefixedData = event.getExtraData().get(IS_PREFIXED_IDENTIFIER);
        if (prefixedData == null) {
            return;
        }
        recipientsHandler.handle(null, event.getRecipients());
    }

}