package fr.army.stelyteam.conversations;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.ColorsBuilder;
import fr.army.stelyteam.utils.EconomyManager;
import fr.army.stelyteam.utils.MessageManager;
import fr.army.stelyteam.utils.SQLManager;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvEditTeamPrefix extends StringPrompt {

    private SQLManager sqlManager;
    private YamlConfiguration config;
    private MessageManager messageManager;
    private EconomyManager economyManager;


    public ConvEditTeamPrefix(StelyTeamPlugin plugin) {
        this.sqlManager = plugin.getSQLManager();
        this.config = plugin.getConfig();
        this.messageManager = plugin.getMessageManager();
        this.economyManager = plugin.getEconomyManager();
    }

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        String authorName = author.getName();
        String teamID = sqlManager.getTeamNameFromPlayerName(authorName);
        
        if (prefixTeamIsTooLong(answer)) {
            // con.getForWhom().sendRawMessage("Le préfixe est trop long");
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.prefix_is_too_long"));
            return this;
        }

        economyManager.removeMoneyPlayer(author, config.getDouble("prices.editTeamPrefix"));
        // con.getForWhom().sendRawMessage("Le préfixe a été changé par " + new ColorsBuilder().replaceColor(answer));
        con.getForWhom().sendRawMessage(messageManager.getReplaceMessage("manage_team.edit_team_prefix.team_prefix_edited", new ColorsBuilder().replaceColor(answer)));
        sqlManager.updateTeamPrefix(teamID, answer);
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        // return "Envoie le nouveau préfixe de team";
        return messageManager.getMessage("manage_team.edit_team_prefix.send_team_prefix");
    }


    private boolean prefixTeamIsTooLong(String prefixTeam){
        Pattern pattern = Pattern.compile("§.");
        Matcher matcher = pattern.matcher(prefixTeam);
        int colors = 0;
        while (matcher.find()) {
            colors++;
        }

        Pattern hexPattern = Pattern.compile("&#[A-Fa-f0-9]{6}");
        Matcher hexMatcher = hexPattern.matcher(prefixTeam);
        int hexColors = 0;
        while (hexMatcher.find()) {
            hexColors++;
        }

        return prefixTeam.length() - (colors * pattern.pattern().length() + hexColors * hexPattern.pattern().length()) > config.getInt("teamPrefixMaxLength");
    }
}