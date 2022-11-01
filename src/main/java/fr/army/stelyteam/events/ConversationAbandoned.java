package fr.army.stelyteam.events;

import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.InactivityConversationCanceller;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.conversation.ConversationSetCanceller;
import fr.army.stelyteam.utils.manager.MessageManager;

public class ConversationAbandoned implements ConversationAbandonedListener {

    private MessageManager messageManager;

    public ConversationAbandoned(StelyTeamPlugin plugin){
        this.messageManager = new MessageManager(plugin);
    }

    @Override
    public void conversationAbandoned(ConversationAbandonedEvent abandonedEvent) {
        ConversationCanceller canceller = abandonedEvent.getCanceller();
        if (canceller == null) return;

        ConversationContext context = abandonedEvent.getContext();

        if (canceller.getClass().equals(InactivityConversationCanceller.class)) {
            // context.getForWhom().sendRawMessage("Temps de réponse dépassé");
            context.getForWhom().sendRawMessage(messageManager.getMessage("conversation.timeout"));
        }else if (canceller.getClass().equals(ConversationSetCanceller.class)){
            // context.getForWhom().sendRawMessage("Action annulée");
            context.getForWhom().sendRawMessage(messageManager.getMessage("conversation.cancel"));
        }
    }
}
