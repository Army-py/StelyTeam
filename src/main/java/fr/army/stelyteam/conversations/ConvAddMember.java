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

public class ConvAddMember extends StringPrompt {

    private StelyTeamPlugin plugin;
    private SQLManager sqlManager;
    private YamlConfiguration config;
    private MessageManager messageManager;


    public ConvAddMember(StelyTeamPlugin plugin){
        this.plugin = plugin;
        this.sqlManager = plugin.getSQLManager();
        this.config = plugin.getConfig();
        this.messageManager = plugin.getMessageManager();
    }


    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        String authorName = author.getName();
        Player player = Bukkit.getPlayer(answer);
        String teamId = sqlManager.getTeamIDFromPlayer(author.getName());
        
        if (player == null) {
            // con.getForWhom().sendRawMessage("Ce joueur n'existe pas");
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.player_not_exist"));
            return null;
        }else if (sqlManager.isMember(answer)) {
            // con.getForWhom().sendRawMessage("Ce joueur est déjà dans une team");
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.player_already_in_team"));
            return null;
        }else if (plugin.containTeamAction(answer, "addMember")) {
            // con.getForWhom().sendRawMessage("Ce joueur a déjà une action en cours");
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.player_already_action"));
            return null;
        }else if (hasReachedMaxMember(teamId)) {
            // con.getForWhom().sendRawMessage("La team est déjà au maximum de membres");
            con.getForWhom().sendRawMessage(messageManager.getMessage("manage_members.add_member.max_members"));
            return null;
        }
        
        plugin.addTeamTempAction(authorName, answer, teamId, "addMember");

        // con.getForWhom().sendRawMessage("L'invitation a été envoyée");


        BaseComponent[] components = new ComponentBuilder(messageManager.getReplaceMessage("manage_members.add_member.invitation_received", authorName))
            .append(messageManager.getMessageWithoutPrefix("manage_members.add_member.accept_invitation")).event(new ClickEvent(Action.RUN_COMMAND, "/st accept"))
            .append(messageManager.getMessageWithoutPrefix("manage_members.add_member.refuse_invitation")).event(new ClickEvent(Action.RUN_COMMAND, "/st deny"))
            .create();

            
        player.spigot().sendMessage(components);
        con.getForWhom().sendRawMessage(messageManager.getMessage("manage_members.add_member.invitation_sent"));
        
        // Inventory inventory = inventoryBuilder.createConfirmInventory();
        // player.openInventory(inventory);

        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        // return "Envoie le pseudo du joueur à ajouter";
        return messageManager.getMessage("manage_members.add_member.send_player_name");
    }


    private boolean hasReachedMaxMember(String teamId) {
        Integer memberAmount = sqlManager.getMembers(teamId).size();
        Integer maxMember = config.getInt("teamMaxMembers");
        Integer teamMembersLelvel = sqlManager.getTeamMembersLevel(teamId);
        return memberAmount >= maxMember + teamMembersLelvel;
    }

}