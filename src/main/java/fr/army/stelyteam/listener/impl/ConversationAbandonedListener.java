package fr.army.stelyteam.listener.impl;

import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.InactivityConversationCanceller;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.builder.conversation.ConversationSetCanceller;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.MessageManager;

public class ConversationAbandonedListener implements org.bukkit.conversations.ConversationAbandonedListener {

    private CacheManager cacheManager;
    private MessageManager messageManager;

    public ConversationAbandonedListener(StelyTeamPlugin plugin){
        this.cacheManager = plugin.getCacheManager();
        this.messageManager = new MessageManager(plugin);
    }

    @Override
    public void conversationAbandoned(ConversationAbandonedEvent abandonedEvent) {
        ConversationCanceller canceller = abandonedEvent.getCanceller();
        ConversationContext context = abandonedEvent.getContext();
        Player author = (Player) context.getForWhom();
        cacheManager.removeInConversation(author.getName());
        
        if (canceller == null) return;


        if (canceller.getClass().equals(InactivityConversationCanceller.class)) {
            author.sendRawMessage(messageManager.getMessage("conversation.timeout"));
        }else if (canceller.getClass().equals(ConversationSetCanceller.class)){
            author.sendRawMessage(messageManager.getMessage("conversation.cancel"));
        }
    }
}
