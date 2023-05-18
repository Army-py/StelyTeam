package fr.army.stelyteam.conversation;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ClickEvent.Action;

public class ConvAddAlliance extends StringPrompt {

    private CacheManager cacheManager;
    private DatabaseManager sqlManager;
    private MessageManager messageManager;


    public ConvAddAlliance(StelyTeamPlugin plugin){
        this.cacheManager = plugin.getCacheManager();
        this.sqlManager = plugin.getDatabaseManager();
        this.messageManager = plugin.getMessageManager();
    }


    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        String authorName = author.getName();
        Team team = Team.initFromPlayerName(authorName);
        
        Team teamAnswer = Team.init(answer);
        if (teamAnswer == null) {
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.team_not_exist"));
            return null;
        }else if (team.isTeamAlliance(teamAnswer.getTeamUuid())) {
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.already_alliance"));
            return null;
        }
        
        UUID teamAllianceUuid = teamAnswer.getTeamUuid();

        BaseComponent[] components = new ComponentBuilder(messageManager.replaceAuthor("manage_alliances.add_alliance.invitation_received", authorName))
            .append(messageManager.getMessageWithoutPrefix("manage_alliances.add_alliance.accept_invitation")).event(new ClickEvent(Action.RUN_COMMAND, "/st accept"))
            .append(messageManager.getMessageWithoutPrefix("manage_alliances.add_alliance.refuse_invitation")).event(new ClickEvent(Action.RUN_COMMAND, "/st deny"))
            .create();

            
        for (String playerName: sqlManager.getTeamMembersWithRank(teamAllianceUuid, 1)){
            Player player = Bukkit.getPlayer(playerName);
            if (player != null && !cacheManager.playerHasActionName(playerName, TemporaryActionNames.ADD_ALLIANCE)) {
                cacheManager.addTempAction(
                    new TemporaryAction(
                        authorName,
                        playerName,
                        TemporaryActionNames.ADD_ALLIANCE,
                        team
                    )
                );
                player.spigot().sendMessage(components);
                con.getForWhom().sendRawMessage(messageManager.getMessage("manage_alliances.add_alliance.invitation_sent"));
                return null;
            }
        }

        con.getForWhom().sendRawMessage(messageManager.getMessage("common.owners_not_connected"));

        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        return messageManager.getMessage("manage_alliances.add_alliance.send_team_name");
    }
}