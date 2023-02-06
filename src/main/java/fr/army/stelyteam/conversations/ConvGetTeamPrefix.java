package fr.army.stelyteam.conversations;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.builder.ColorsBuilder;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.EconomyManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.MySQLManager;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;


public class ConvGetTeamPrefix extends StringPrompt {

    private CacheManager cacheManager;
    private YamlConfiguration config;
    private MessageManager messageManager;
    private EconomyManager economyManager;
    private ColorsBuilder colorBuilder;


    public ConvGetTeamPrefix(StelyTeamPlugin plugin) {
        this.cacheManager = plugin.getCacheManager();
        this.config = plugin.getConfig();
        this.messageManager = plugin.getMessageManager();
        this.economyManager = plugin.getEconomyManager();
        this.colorBuilder = new ColorsBuilder(plugin);
    }

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        String authorName = author.getName();
        
        if (colorBuilder.prefixTeamIsTooLong(answer)) {
            // con.getForWhom().sendRawMessage("Le préfixe est trop long");
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.prefix_is_too_long"));
            return this;
        }else if (colorBuilder.containsBlockedColors(answer)) {
            // con.getForWhom().sendRawMessage("Le préfixe contient des couleurs interdites");
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.prefix_contains_blocked_colors"));
            return this;
        }


        cacheManager.setActionTeamPrefix(authorName, answer);

        TemporaryAction tempAction = cacheManager.getTempAction(authorName);
        Team team = tempAction.getTeam();

        team.createTeam();
        cacheManager.removePlayerAction(authorName);
        economyManager.removeMoneyPlayer(author, config.getDouble("prices.createTeam"));
        con.getForWhom().sendRawMessage(messageManager.getMessage("manage_team.creation.team_created"));
        
        if (config.getBoolean("openTeamAfterCreate")) StelyTeamPlugin.getPlugin().openMainInventory(author, team);
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        // return "Envoie le préfixe de team";
        return messageManager.getMessage("manage_team.creation.send_team_prefix");
    }
}