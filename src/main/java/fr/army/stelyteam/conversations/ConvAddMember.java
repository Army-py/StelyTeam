package fr.army.stelyteam.conversations;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.MySQLManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ClickEvent.Action;

public class ConvAddMember extends StringPrompt {

    private StelyTeamPlugin plugin;
    private CacheManager cacheManager;
    private MySQLManager sqlManager;
    private YamlConfiguration config;
    private MessageManager messageManager;


    public ConvAddMember(StelyTeamPlugin plugin){
        this.plugin = plugin;
        this.cacheManager = plugin.getCacheManager();
        this.sqlManager = plugin.getSQLManager();
        this.config = plugin.getConfig();
        this.messageManager = plugin.getMessageManager();
    }


    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        String authorName = author.getName();
        Player player = Bukkit.getPlayer(answer);
        String teamId = sqlManager.getTeamNameFromPlayerName(author.getName());
        Team team = sqlManager.getTeamFromPlayerName(author.getName());
        
        if (player == null) {
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.player_not_exist"));
            return null;
        }else if (sqlManager.isMember(answer)) {
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.player_already_in_team"));
            return null;
        }else if (cacheManager.playerHasActionName(answer, TemporaryActionNames.ADD_MEMBER)) {
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.player_already_action"));
            return null;
        }else if (hasReachedMaxMember(teamId)) {
            con.getForWhom().sendRawMessage(messageManager.getMessage("manage_members.add_member.max_members"));
            return null;
        }
        
        cacheManager.addTempAction(
            new TemporaryAction(
                authorName,
                answer,
                TemporaryActionNames.ADD_MEMBER,
                team)
        );


        BaseComponent[] components = new ComponentBuilder(messageManager.getReplaceMessage("manage_members.add_member.invitation_received", authorName))
            .append(messageManager.getMessageWithoutPrefix("manage_members.add_member.accept_invitation")).event(new ClickEvent(Action.RUN_COMMAND, "/st accept"))
            .append(messageManager.getMessageWithoutPrefix("manage_members.add_member.refuse_invitation")).event(new ClickEvent(Action.RUN_COMMAND, "/st deny"))
            .create();

            
        player.spigot().sendMessage(components);
        con.getForWhom().sendRawMessage(messageManager.getMessage("manage_members.add_member.invitation_sent"));

        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        return messageManager.getMessage("manage_members.add_member.send_player_name");
    }


    private boolean hasReachedMaxMember(String teamId) {
        Integer memberAmount = sqlManager.getTeamMembers(teamId).size();
        Integer maxMember = config.getInt("teamMaxMembers");
        Integer teamMembersLelvel = sqlManager.getImprovLvlMembers(teamId);
        return memberAmount >= maxMember + teamMembersLelvel;
    }

}