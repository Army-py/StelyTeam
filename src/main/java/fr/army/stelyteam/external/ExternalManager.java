package fr.army.stelyteam.external;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.bukkit.plugin.Plugin;

import fr.army.stelyteam.external.bungeechatconnect.ExternalBCCLoader;
import fr.army.stelyteam.external.bungeechatconnect.handler.RecipientsHandler;
import fr.army.stelyteam.external.stelyclaim.StelyClaimLoader;

public class ExternalManager {

    private final List<MessageHandler> messageHandlers;

    public ExternalManager() {
        messageHandlers = new LinkedList<>();
    }
    
    public void load(Plugin plugin, RecipientsHandler recipientsHandler){
        StelyClaimLoader stelyClaimLoader = new StelyClaimLoader();
        // stelyClaimLoader.load();

        ExternalBCCLoader essentialsBBCLoader = new ExternalBCCLoader();
        registerHandler(essentialsBBCLoader.load(plugin, recipientsHandler));
    }

    public void unload(){
        StelyClaimLoader stelyClaimLoader = new StelyClaimLoader();
        // stelyClaimLoader.unload();
    }

    private void registerHandler(MessageHandler messageHandler) {
        if (messageHandler != null) {
            messageHandlers.add(messageHandler);
        }
    }

    public void registerMessage(UUID senderId, boolean isPrefixed) {
        for (MessageHandler messageHandler : messageHandlers) {
            messageHandler.handleMessage(senderId, isPrefixed);
        }
    }
}
