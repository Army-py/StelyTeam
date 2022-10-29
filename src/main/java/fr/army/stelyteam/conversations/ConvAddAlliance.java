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
        String teamName = sqlManager.getTeamNameFromPlayerName(authorName);
        String ownerName = sqlManager.getTeamOwnerName(answer);
        
        if (!sqlManager.teamNameExists(answer)) {
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.team_not_exist"));
            return null;
        }else if (sqlManager.isAlliance(teamName, answer)) {
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.already_alliance"));
            return null;
        }
        // }else if (plugin.containTeamAction(ownerName, "addAlliance")) {
        //     con.getForWhom().sendRawMessage(messageManager.getMessage("common.owner_already_action"));
        //     return null;
        // }
        

        BaseComponent[] components = new ComponentBuilder(messageManager.getReplaceMessage("manage_alliances.add_alliance.invitation_received", authorName))
            .append(messageManager.getMessageWithoutPrefix("manage_alliances.add_alliance.accept_invitation")).event(new ClickEvent(Action.RUN_COMMAND, "/st accept"))
            .append(messageManager.getMessageWithoutPrefix("manage_alliances.add_alliance.refuse_invitation")).event(new ClickEvent(Action.RUN_COMMAND, "/st deny"))
            .create();

            
        for (String playerName: sqlManager.getTeamMembersWithRank(answer, 1)){
            Player player = Bukkit.getPlayer(playerName);
            if (player != null && !plugin.containTeamAction(playerName, "addAlliance")) {
                plugin.addTeamTempAction(authorName, playerName, teamName, "addAlliance");
                player.spigot().sendMessage(components);
                con.getForWhom().sendRawMessage(messageManager.getMessage("manage_alliances.add_alliance.invitation_sent"));
                break;
            }
        }

        con.getForWhom().sendRawMessage(messageManager.getMessage("common.owners_not_connected"));
        
        // Inventory inventory = inventoryBuilder.createConfirmInventory();
        // player.openInventory(inventory);

        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        // return "Envoie le pseudo du joueur à ajouter";
        return messageManager.getMessage("manage_alliances.add_alliance.send_team_name");
    }
}