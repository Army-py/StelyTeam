package fr.army.stelyteam.conversations;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.conversation.ConversationBuilder;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.SQLManager;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class ConvGetTeamId extends StringPrompt {

    private StelyTeamPlugin plugin;
    private SQLManager sqlManager;
    private YamlConfiguration config;
    private MessageManager messageManager;
    private ConversationBuilder conversationBuilder;


    public ConvGetTeamId(StelyTeamPlugin plugin) {
        this.plugin = plugin;
        this.sqlManager = plugin.getSQLManager();
        this.config = plugin.getConfig();
        this.messageManager = plugin.getMessageManager();
        this.conversationBuilder = plugin.getConversationBuilder();
    }

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        String authorName = author.getName();

        if (nameTeamIsTooLong(answer)) {
            // con.getForWhom().sendRawMessage("Le nom est trop long");
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.name_is_too_long"));
            return this;
        }else if (sqlManager.teamNameExists(answer)){
            // con.getForWhom().sendRawMessage("Ce nom de team existe déjà");
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.name_already_exists"));
            return this;
        }else if (answer.contains(" ")){
            // con.getForWhom().sendRawMessage("Le nom ne doit pas contenir d'espace");
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.name_cannot_contain_space"));
            return this;
        }


        if (plugin.getCreationTeamTemp(authorName) != null){
            plugin.removeCreationTeamTemp(authorName);
        }
        plugin.addCreationTeamTempName(authorName, answer);
        conversationBuilder.getNameInput(author, new ConvGetTeamPrefix(plugin));
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        // return "Envoie le nom de team";
        return messageManager.getMessage("manage_team.creation.send_team_id");
    }


    private boolean nameTeamIsTooLong(String teamName){
        return teamName.length() > config.getInt("teamNameMaxLength");
    }
}