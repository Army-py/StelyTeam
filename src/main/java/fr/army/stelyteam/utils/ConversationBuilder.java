package fr.army.stelyteam.utils;

import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.ConversationPrefix;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;

public class ConversationBuilder {
    public void getNameInput(Player player, Prompt prompt) {
        ConversationPrefix prefix = new ConversationPrefix() {
            @Override
            public String getPrefix(ConversationContext context) {
                return StelyTeamPlugin.config.getString("prefix");
            }
        };

        ConversationFactory cf = new ConversationFactory(StelyTeamPlugin.instance);
        cf.withFirstPrompt(prompt);
        cf.withLocalEcho(false);
        cf.withTimeout(StelyTeamPlugin.config.getInt("conversationTimeout"));
        cf.withPrefix(prefix);

        Conversation conv = cf.buildConversation(player);
        conv.begin();
        return;
    }
}
