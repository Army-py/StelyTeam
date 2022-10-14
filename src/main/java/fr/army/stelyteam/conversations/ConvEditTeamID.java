package fr.army.stelyteam.conversations;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.EconomyManager;
import fr.army.stelyteam.utils.MessageManager;
import fr.army.stelyteam.utils.SQLManager;
import fr.army.stelyteam.utils.SQLiteManager;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class ConvEditTeamID extends StringPrompt {

    private SQLManager sqlManager;
    private SQLiteManager sqliteManager;
    private YamlConfiguration config;
    private MessageManager messageManager;
    private EconomyManager economyManager;


    public ConvEditTeamID(StelyTeamPlugin plugin){
        this.sqlManager = plugin.getSQLManager();
        this.sqliteManager = plugin.getSQLiteManager();
        this.config = plugin.getConfig();
        this.messageManager = plugin.getMessageManager();
        this.economyManager = plugin.getEconomyManager();
    }

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        String authorName = author.getName();
        String teamID = sqlManager.getTeamNameFromPlayerName(authorName);

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

        economyManager.removeMoneyPlayer(author, config.getDouble("prices.editTeamId"));
        // con.getForWhom().sendRawMessage("Le nom a été changé par " + answer);
        con.getForWhom().sendRawMessage(messageManager.getReplaceMessage("manage_team.edit_team_id.team_name_edited", answer));
        sqlManager.updateTeamName(teamID, answer);
        sqliteManager.updateTeamID(teamID, answer);
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        // return "Envoie le nouveau nom de team";
        return messageManager.getMessage("manage_team.edit_team_id.send_team_id");
    }


    private boolean nameTeamIsTooLong(String teamName){
        return teamName.length() > config.getInt("teamNameMaxLength");
    }
}