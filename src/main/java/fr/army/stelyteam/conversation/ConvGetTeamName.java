package fr.army.stelyteam.conversation;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.builder.conversation.ConversationBuilder;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class ConvGetTeamName extends StringPrompt {

    private StelyTeamPlugin plugin;
    private CacheManager cacheManager;
    private DatabaseManager sqlManager;
    private YamlConfiguration config;
    private MessageManager messageManager;
    private ConversationBuilder conversationBuilder;


    public ConvGetTeamName(StelyTeamPlugin plugin) {
        this.plugin = plugin;
        this.cacheManager = plugin.getCacheManager();
        this.sqlManager = plugin.getDatabaseManager();
        this.config = plugin.getConfig();
        this.messageManager = plugin.getMessageManager();
        this.conversationBuilder = new ConversationBuilder(plugin);
    }

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        String authorName = author.getName();

        if (nameTeamIsTooLong(answer)) {
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.name_is_too_long"));
            return this;
        }else if (sqlManager.teamNameExists(answer)){
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.name_already_exists"));
            return this;
        }else if (answer.contains(" ")){
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.name_cannot_contain_space"));
            return this;
        }


        if (cacheManager.playerHasAction(authorName)){
            cacheManager.removePlayerAction(authorName);
        }
        cacheManager.addTempAction(
            new TemporaryAction(
                authorName, 
                new Team(answer, authorName)
            )
        );
        conversationBuilder.getNameInput(author, new ConvGetTeamPrefix(plugin));
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        return messageManager.getMessage("manage_team.creation.send_team_id");
    }


    private boolean nameTeamIsTooLong(String teamName){
        return teamName.length() > config.getInt("teamNameMaxLength");
    }
}