package fr.army.stelyteam.conversations;

import fr.army.stelyteam.StelyTeamPlugin;
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

public class ConvGetTeamPrefix extends StringPrompt {

    private StelyTeamPlugin plugin;
    private SQLManager sqlManager;
    private YamlConfiguration config;
    private MessageManager messageManager;
    private EconomyManager economyManager;


    public ConvGetTeamPrefix(StelyTeamPlugin plugin) {
        this.plugin = plugin;
        this.sqlManager = plugin.getSQLManager();
        this.config = plugin.getConfig();
        this.messageManager = plugin.getMessageManager();
        this.economyManager = plugin.getEconomyManager();
    }

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        String authorName = author.getName();
        
        if (prefixTeamIsTooLong(answer)) {
            // con.getForWhom().sendRawMessage("Le préfixe est trop long");
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.prefix_is_too_long"));
            return this;
        }


        plugin.addCreationTeamTempPrefix(authorName, answer);

        String[] teamInfos = plugin.getCreationTeamTemp(authorName);

        sqlManager.insertTeam(teamInfos[1], teamInfos[2], authorName);
        plugin.removeCreationTeamTemp(authorName);
        economyManager.removeMoneyPlayer(author, config.getDouble("prices.createTeam"));
        con.getForWhom().sendRawMessage(messageManager.getMessage("manage_team.creation.team_created"));
        
        openTeamPanel(author);
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        // return "Envoie le préfixe de team";
        return messageManager.getMessage("manage_team.creation.send_team_prefix");
    }


    private boolean prefixTeamIsTooLong(String prefixTeam){
        Pattern pattern = Pattern.compile("&.");
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

    private void openTeamPanel(Player player) {
        if (config.getBoolean("openTeamAfterCreate")){
            player.performCommand("st");
        }
    }
}