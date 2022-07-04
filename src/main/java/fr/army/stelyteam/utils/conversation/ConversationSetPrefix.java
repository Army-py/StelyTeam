package fr.army.stelyteam.utils.conversation;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationPrefix;

import fr.army.stelyteam.StelyTeamPlugin;

public class ConversationSetPrefix implements ConversationPrefix {

    private YamlConfiguration messages;

    public ConversationSetPrefix(StelyTeamPlugin plugin) {
        this.messages = plugin.getMessages();
    }

    @Override
    public String getPrefix(ConversationContext context) {
        return messages.getString("prefix");
    }
}
