package fr.army.stelyteam.conversation.old;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.config.Config;
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

public class ConvAddMember extends StringPrompt {

    private CacheManager cacheManager;
    private DatabaseManager sqlManager;


    public ConvAddMember(StelyTeamPlugin plugin){
        this.cacheManager = plugin.getCacheManager();
        this.sqlManager = plugin.getDatabaseManager();
    }


    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        final Player author = (Player) con.getForWhom();
        final String authorName = author.getName();
        final String displayName = author.getDisplayName();
        Player player = Bukkit.getPlayer(answer);
        Team team = Team.initFromPlayerName(authorName);
        
        if (player == null) {
            con.getForWhom().sendRawMessage(Messages.PREFIX.getMessage() + Messages.PLAYER_NOT_IN_THIS_TEAM.getMessage());
            return null;
        }else if (sqlManager.isMember(answer)) {
            con.getForWhom().sendRawMessage(Messages.PREFIX.getMessage() + Messages.PLAYER_ALREADY_IN_TEAM.getMessage());
            return null;
        }else if (cacheManager.playerHasActionName(answer, TemporaryActionNames.ADD_MEMBER)) {
            con.getForWhom().sendRawMessage(Messages.PREFIX.getMessage() + Messages.PLAYER_REPONDS_INVITATION.getMessage());
            return null;
        }else if (hasReachedMaxMember(team)) {
            con.getForWhom().sendRawMessage(Messages.PREFIX.getMessage() + Messages.TEAM_REACHED_MAX_MEMBERS.getMessage());
            return null;
        }
        
        cacheManager.addTempAction(
            new TemporaryAction(
                authorName,
                answer,
                TemporaryActionNames.ADD_MEMBER,
                team)
        );

        Map<Placeholders, String> replaces = new HashMap<>();
        replaces.put(Placeholders.PLAYER_NAME, authorName);
        replaces.put(Placeholders.PLAYER_DISPLAY_NAME, displayName);
        BaseComponent[] components = new ComponentBuilder(Messages.PREFIX.getMessage() + Messages.MEMBER_INVITATION_RECEIVED.getMessage(replaces))
            .append(Messages.ACCEPT_MEMBER_INVITATION.getMessage()).event(new ClickEvent(Action.RUN_COMMAND, "/st accept"))
            .append(Messages.REFUSE_MEMBER_INVITATION.getMessage()).event(new ClickEvent(Action.RUN_COMMAND, "/st deny"))
            .create();

            
        player.spigot().sendMessage(components);
        con.getForWhom().sendRawMessage(Messages.PREFIX.getMessage() + Messages.MEMBER_INVITATION_SENT.getMessage());

        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        return Messages.PREFIX.getMessage() + Messages.SEND_PLAYER_NAME_TO_ADD.getMessage();
    }


    private boolean hasReachedMaxMember(Team team) {
        final int memberAmount = team.getTeamMembers().size();
        final int maxMember = Config.teamMaxMembersLimit;
        final int teamMembersLelvel = team.getImprovLvlMembers();
        return memberAmount >= maxMember + teamMembersLelvel;
    }

}