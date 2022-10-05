package fr.army.stelyteam.conversations;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.MessageManager;
import fr.army.stelyteam.utils.SQLManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ClickEvent.Action;

public class ConvAddAlliance extends StringPrompt {

    private StelyTeamPlugin plugin;
    private SQLManager sqlManager;
    private YamlConfiguration config;
    private MessageManager messageManager;


    public ConvAddAlliance(StelyTeamPlugin plugin){
        this.plugin = plugin;
        this.sqlManager = plugin.getSQLManager();
        this.config = plugin.getConfig();
        this.messageManager = plugin.getMessageManager();
    }


    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        String authorName = author.getName();
        String teamId = sqlManager.getTeamIDFromPlayer(author.getName());
        String ownerName = sqlManager.getTeamOwner(teamId);
        Player owner = Bukkit.getPlayer(ownerName);
        
        if (!sqlManager.teamIdExist(answer)) {
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.team_not_exist"));
            return null;
        }else if (sqlManager.isAlliance(teamId, answer)) {
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.already_alliance"));
            return null;
        }else if (owner == null) {
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.owner_not_connected"));
            return null;
        }else if (plugin.containTeamAction(ownerName, "addAlliance")) {
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.owner_already_action"));
            return null;
        }
        
        plugin.addTeamTempAction(authorName, ownerName, teamId, "addAlliance");


        BaseComponent[] components = new ComponentBuilder(messageManager.getReplaceMessage("manage_alliances.add_alliance.invitation_received", authorName))
            .append(messageManager.getMessageWithoutPrefix("manage_alliances.add_alliance.accept_invitation")).event(new ClickEvent(Action.RUN_COMMAND, "/st accept"))
            .append(messageManager.getMessageWithoutPrefix("manage_alliances.add_alliance.refuse_invitation")).event(new ClickEvent(Action.RUN_COMMAND, "/st deny"))
            .create();

            
        owner.spigot().sendMessage(components);
        con.getForWhom().sendRawMessage(messageManager.getMessage("manage_alliances.add_alliance.invitation_sent"));
        
        // Inventory inventory = inventoryBuilder.createConfirmInventory();
        // player.openInventory(inventory);

        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        // return "Envoie le pseudo du joueur à ajouter";
        return messageManager.getMessage("manage_alliances.add_alliance.send_player_name");
    }


    private boolean hasReachedMaxMember(String teamId) {
        Integer memberAmount = sqlManager.getMembers(teamId).size();
        Integer maxMember = config.getInt("teamMaxMembers");
        Integer teamMembersLelvel = sqlManager.getTeamMembersLevel(teamId);
        return memberAmount >= maxMember + teamMembersLelvel;
    }

}