package fr.army.stelyteam.conversations;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.builder.ColorsBuilder;
import fr.army.stelyteam.utils.manager.EconomyManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.MySQLManager;

import org.bukkit.Color;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvEditTeamPrefix extends StringPrompt {

    private MySQLManager sqlManager;
    private YamlConfiguration config;
    private MessageManager messageManager;
    private EconomyManager economyManager;
    private ColorsBuilder colorBuilder;


    public ConvEditTeamPrefix(StelyTeamPlugin plugin) {
        this.sqlManager = plugin.getSQLManager();
        this.config = plugin.getConfig();
        this.messageManager = plugin.getMessageManager();
        this.economyManager = plugin.getEconomyManager();
        this.colorBuilder = new ColorsBuilder(plugin);
    }

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        String authorName = author.getName();
        Team team = sqlManager.getTeamFromPlayerName(authorName);
        
        if (colorBuilder.prefixTeamIsTooLong(answer)) {
            // con.getForWhom().sendRawMessage("Le préfixe est trop long");
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.prefix_is_too_long"));
            return this;
        }else if (colorBuilder.containsBlockedColors(answer)) {
            // con.getForWhom().sendRawMessage("Le préfixe contient des couleurs interdites");
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.prefix_contains_blocked_colors"));
            return this;
        }

        economyManager.removeMoneyPlayer(author, config.getDouble("prices.editTeamPrefix"));
        // con.getForWhom().sendRawMessage("Le préfixe a été changé par " + new ColorsBuilder().replaceColor(answer));
        con.getForWhom().sendRawMessage(messageManager.getReplaceMessage("manage_team.edit_team_prefix.team_prefix_edited", colorBuilder.replaceColor(answer)));
        team.updateTeamPrefix(answer);
        team.refreshTeamMembersInventory(authorName);
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        // return "Envoie le nouveau préfixe de team";
        return messageManager.getMessage("manage_team.edit_team_prefix.send_team_prefix");
    }
}