package fr.army.stelyteam.utils.conversation;

import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.ConversationContext;

import fr.army.stelyteam.StelyTeamPlugin;

public class ConversationSetCanceller implements ConversationCanceller {
    @Override
    public void setConversation(Conversation conversation) {
        return;
    }

    @Override
    public boolean cancelBasedOnInput(ConversationContext context, String input) {
        return StelyTeamPlugin.config.getStringList("conversationCancelWords").contains(input);
        // return input.equalsIgnoreCase("cancel");
    }

    @Override
    public ConversationCanceller clone() {
        return this;
    }
}
