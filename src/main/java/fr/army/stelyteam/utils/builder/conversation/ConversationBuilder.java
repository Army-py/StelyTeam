package fr.army.stelyteam.utils.builder.conversation;

import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.config.Config;
import fr.army.stelyteam.listener.impl.ConversationAbandonedListener;
import fr.army.stelyteam.utils.manager.CacheManager;

public class ConversationBuilder {

    private StelyTeamPlugin plugin;
    private CacheManager cacheManager;

    public ConversationBuilder(StelyTeamPlugin plugin) {
        this.plugin = plugin;
        this.cacheManager = plugin.getCacheManager();
    }

    public void getNameInput(Player player, Prompt prompt) {
        ConversationFactory cf = new ConversationFactory(plugin);

        cf.withFirstPrompt(prompt);
        cf.withLocalEcho(false);
        cf.addConversationAbandonedListener(new ConversationAbandonedListener(plugin));
        cf.withConversationCanceller(new ConversationSetCanceller());
        cf.withTimeout(Config.conversationTimeout);
        // cf.withEscapeSequence("cancel");
        // cf.withPrefix(new ConversationSetPrefix());

        cacheManager.addInConversation(player.getName());
        Conversation conv = cf.buildConversation(player);
        conv.begin();
        return;
    }
}
