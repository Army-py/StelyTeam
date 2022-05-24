package fr.army.stelyteam.utils.conversation;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationPrefix;

import fr.army.stelyteam.StelyTeamPlugin;

public class ConversationSetPrefix implements ConversationPrefix {
    @Override
    public String getPrefix(ConversationContext context) {
        return StelyTeamPlugin.config.getString("prefix");
    }
}
