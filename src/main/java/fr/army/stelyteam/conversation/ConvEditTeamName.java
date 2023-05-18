package fr.army.stelyteam.conversation;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.manager.EconomyManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class ConvEditTeamName extends StringPrompt {

    private DatabaseManager sqlManager;
    private YamlConfiguration config;
    private MessageManager messageManager;
    private EconomyManager economyManager;


    public ConvEditTeamName(StelyTeamPlugin plugin){
        this.sqlManager = plugin.getDatabaseManager();
        this.config = plugin.getConfig();
        this.messageManager = plugin.getMessageManager();
        this.economyManager = plugin.getEconomyManager();
    }

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        String authorName = author.getName();
        Team team = sqlManager.getTeamFromPlayerName(authorName);

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

        economyManager.removeMoneyPlayer(author, config.getDouble("prices.editTeamId"));
        con.getForWhom().sendRawMessage(messageManager.getReplaceMessage("manage_team.edit_team_id.team_name_edited", answer));
        team.updateTeamName(answer);
        team.refreshTeamMembersInventory(authorName);
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        return messageManager.getMessage("manage_team.edit_team_id.send_team_id");
    }


    private boolean nameTeamIsTooLong(String teamName){
        return teamName.length() > config.getInt("teamNameMaxLength");
    }
}