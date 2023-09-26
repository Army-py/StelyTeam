package fr.army.stelyteam.utils.builder.conversation;

import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.ConversationContext;

import fr.army.stelyteam.config.Config;

public class ConversationSetCanceller implements ConversationCanceller {


    public ConversationSetCanceller() {
    }

    @Override
    public void setConversation(Conversation conversation) {
        return;
    }

    @Override
    public boolean cancelBasedOnInput(ConversationContext context, String input) {
        return Config.conversationCancelWords.contains(input);
    }

    @Override
    public ConversationCanceller clone() {
        return this;
    }
}
