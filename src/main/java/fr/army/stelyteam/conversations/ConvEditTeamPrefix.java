package fr.army.stelyteam.conversations;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.ColorsCreator;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvEditTeamPrefix extends StringPrompt {

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        String authorName = author.getName();
        
        if (prefixTeamIsTooLong(answer)) {
            con.getForWhom().sendRawMessage("Le préfixe est trop long");
            return this;
        }

        String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(authorName);

        con.getForWhom().sendRawMessage("Le préfixe a été changé par " + new ColorsCreator().colourise(answer));
        StelyTeamPlugin.sqlManager.updateTeamPrefix(teamID, answer, authorName);
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        return "Envoie le nouveau préfixe de team";
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

        return prefixTeam.length() - (colors * pattern.pattern().length() + hexColors * hexPattern.pattern().length()) > StelyTeamPlugin.config.getInt("teamPrefixMaxLength");
    }
}