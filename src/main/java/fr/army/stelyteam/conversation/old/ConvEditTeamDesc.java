package fr.army.stelyteam.conversation.old;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.builder.ColorsBuilder;
import fr.army.stelyteam.utils.manager.EconomyManager;
import fr.army.stelyteam.utils.manager.MessageManager;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;


public class ConvEditTeamDesc extends StringPrompt {

    private YamlConfiguration config;
    private MessageManager messageManager;
    private EconomyManager economyManager;
    private ColorsBuilder colorBuilder;


    public ConvEditTeamDesc(StelyTeamPlugin plugin) {
        this.config = plugin.getConfig();
        this.messageManager = plugin.getMessageManager();
        this.economyManager = plugin.getEconomyManager();
        this.colorBuilder = new ColorsBuilder(plugin);
    }

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        String authorName = author.getName();
        Team team = Team.initFromPlayerName(authorName);
        
        if (colorBuilder.descriptionTeamIsTooLong(answer)) {
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.description_is_too_long"));
            return this;
        }else if (colorBuilder.containsBlockedColors(answer)) {
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.description_contains_blocked_colors"));
            return this;
        }

        economyManager.removeMoneyPlayer(author, config.getDouble("prices.editTeamDescription"));
        con.getForWhom().sendRawMessage(messageManager.getReplaceMessage("manage_team.edit_team_description.team_description_edited", colorBuilder.replaceColor(answer)));
        team.updateTeamDescription(answer);
        team.refreshTeamMembersInventory(authorName);
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        return messageManager.getMessage("manage_team.edit_team_description.send_team_description");
    }
}