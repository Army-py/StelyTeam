package fr.army.stelyteam.utils.conversation;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.ConversationContext;

import fr.army.stelyteam.StelyTeamPlugin;

public class ConversationSetCanceller implements ConversationCanceller {

    private YamlConfiguration config;

    public ConversationSetCanceller(StelyTeamPlugin plugin) {
        this.config = plugin.getConfig();
    }

    @Override
    public void setConversation(Conversation conversation) {
        return;
    }

    @Override
    public boolean cancelBasedOnInput(ConversationContext context, String input) {
        return config.getStringList("conversationCancelWords").contains(input);
    }

    @Override
    public ConversationCanceller clone() {
        return this;
    }
}
