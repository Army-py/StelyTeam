package fr.army.stelyteam.utils.conversation;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.events.ConversationAbandoned;

public class ConversationBuilder {

    private StelyTeamPlugin plugin;
    private YamlConfiguration config;

    public ConversationBuilder(StelyTeamPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public void getNameInput(Player player, Prompt prompt) {
        ConversationFactory cf = new ConversationFactory(plugin);

        cf.withFirstPrompt(prompt);
        cf.withLocalEcho(false);
        cf.addConversationAbandonedListener(new ConversationAbandoned(plugin));
        cf.withConversationCanceller(new ConversationSetCanceller(plugin));
        cf.withTimeout(config.getInt("conversationTimeout"));
        // cf.withEscapeSequence("cancel");
        // cf.withPrefix(new ConversationSetPrefix());

        Conversation conv = cf.buildConversation(player);
        conv.begin();
        return;
    }
}
