package fr.army.stelyteam.conversations;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.events.menu.ConfirmInventory;
import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;

public class ConvRemoveMember extends StringPrompt {

    private CacheManager cacheManager;
    private DatabaseManager sqlManager;
    private MessageManager messageManager;


    public ConvRemoveMember(StelyTeamPlugin plugin) {
        this.cacheManager = plugin.getCacheManager();
        this.sqlManager = plugin.getDatabaseManager();
        this.messageManager = plugin.getMessageManager();
    }


    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        String authorName = author.getName();
        Team team = sqlManager.getTeamFromPlayerName(author.getName());

        if (!team.isTeamMember(answer)){
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.player_not_in_your_team"));
            return null;
        }else if (authorName.equals(answer)){
            con.getForWhom().sendRawMessage(messageManager.getMessage("manage_members.remove_member.cant_exclude_yourself"));
            return null;
        }else if (team.getMemberRank(answer) <= team.getMemberRank(authorName)){
            con.getForWhom().sendRawMessage(messageManager.getMessage("manage_members.remove_member.cant_exclude_higher_rank"));
            return null;
        }
        
        cacheManager.addTempAction(
            new TemporaryAction(
                authorName,
                answer,
                TemporaryActionNames.REMOVE_MEMBER,
                team)
        );
        new ConfirmInventory(author).openMenu();
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        return messageManager.getMessage("manage_members.remove_member.send_player_name");
    }

}