package fr.army.stelyteam.events;

import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.InactivityConversationCanceller;

import fr.army.stelyteam.utils.conversation.ConversationSetCanceller;

public class ConversationAbandoned implements ConversationAbandonedListener {

    @Override
    public void conversationAbandoned(ConversationAbandonedEvent abandonedEvent) {
        ConversationCanceller canceller = abandonedEvent.getCanceller();
        if (canceller == null) return;

        ConversationContext context = abandonedEvent.getContext();

        if (canceller.getClass().equals(InactivityConversationCanceller.class)) {
            context.getForWhom().sendRawMessage("Temps de réponse dépassé");
        }else if (canceller.getClass().equals(ConversationSetCanceller.class)){
            context.getForWhom().sendRawMessage("Action annulée");
        }
    }
}
