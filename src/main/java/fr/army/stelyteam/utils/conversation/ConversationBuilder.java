package fr.army.stelyteam.utils.conversation;

import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.events.ConversationAbandoned;

public class ConversationBuilder {
    public void getNameInput(Player player, Prompt prompt) {
        ConversationFactory cf = new ConversationFactory(StelyTeamPlugin.instance);

        cf.withFirstPrompt(prompt);
        cf.withLocalEcho(false);
        cf.withTimeout(StelyTeamPlugin.config.getInt("conversationTimeout"));
        cf.addConversationAbandonedListener(new ConversationAbandoned());
        cf.withConversationCanceller(new ConversationSetCanceller());
        // cf.withPrefix(new ConversationSetPrefix());

        Conversation conv = cf.buildConversation(player);
        conv.begin();
        return;
    }
}
