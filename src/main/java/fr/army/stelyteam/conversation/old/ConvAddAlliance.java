package fr.army.stelyteam.conversation.old;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.config.message.Messages;
import fr.army.stelyteam.config.message.Placeholders;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class ConvAddAlliance extends StringPrompt {

    private CacheManager cacheManager;
    private DatabaseManager sqlManager;


    public ConvAddAlliance(StelyTeamPlugin plugin){
        this.cacheManager = plugin.getCacheManager();
        this.sqlManager = plugin.getDatabaseManager();
    }


    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        final Player author = (Player) con.getForWhom();
        final String authorName = author.getName();
        final String displayName = author.getDisplayName();
        Team team = Team.initFromPlayerName(authorName);
        
        Team teamAnswer = Team.init(answer);
        if (teamAnswer == null) {
            con.getForWhom().sendRawMessage(Messages.PREFIX.getMessage() + Messages.TEAM_DOES_NOT_EXIST.getMessage());
            return null;
        }else if (team.isTeamAlliance(teamAnswer.getTeamUuid())) {
            con.getForWhom().sendRawMessage(Messages.PREFIX.getMessage() + Messages.ALREADY_ALLIED_WITH_TEAM.getMessage());
            return null;
        }
        
        UUID teamAllianceUuid = teamAnswer.getTeamUuid();

        Map<Placeholders, String> replaces = new HashMap<>();
        replaces.put(Placeholders.PLAYER_NAME, authorName);
        replaces.put(Placeholders.PLAYER_DISPLAY_NAME, displayName);
        BaseComponent[] components = new ComponentBuilder(Messages.PREFIX.getMessage() + Messages.ALLIANCE_INVITATION_RECEIVED.getMessage(replaces))
            .append(Messages.ACCEPT_ALLIANCE_INVITATION.getMessage()).event(new ClickEvent(Action.RUN_COMMAND, "/st accept"))
            .append(Messages.REFUSE_ALLIANCE_INVITATION.getMessage()).event(new ClickEvent(Action.RUN_COMMAND, "/st deny"))
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
                con.getForWhom().sendRawMessage(Messages.PREFIX.getMessage() + Messages.ALLIANCE_INVITATION_SENT.getMessage());
                return null;
            }
        }

        con.getForWhom().sendRawMessage(Messages.PREFIX.getMessage() + Messages.TEAM_OWNERS_OFFLINE.getMessage());

        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        return Messages.PREFIX.getMessage() + Messages.SEND_TEAM_NAME.getMessage();
    }
}