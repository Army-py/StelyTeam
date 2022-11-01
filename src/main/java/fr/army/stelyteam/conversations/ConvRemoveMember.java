package fr.army.stelyteam.conversations;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.builder.InventoryBuilder;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.SQLManager;

public class ConvRemoveMember extends StringPrompt {

    private StelyTeamPlugin plugin;
    private SQLManager sqlManager;
    private MessageManager messageManager;
    private InventoryBuilder inventoryBuilder;


    public ConvRemoveMember(StelyTeamPlugin plugin) {
        this.plugin = plugin;
        this.sqlManager = plugin.getSQLManager();
        this.messageManager = plugin.getMessageManager();
        this.inventoryBuilder = plugin.getInventoryBuilder();
    }


    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        String authorName = author.getName();
        String teamId = sqlManager.getTeamNameFromPlayerName(author.getName());

        if (!sqlManager.isMemberInTeam(answer, teamId)){
            // con.getForWhom().sendRawMessage("Le joueur n'est pas dans ta team");
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.player_not_in_your_team"));
            return null;
        }else if (authorName.equals(answer)){
            // con.getForWhom().sendRawMessage("Tu ne peux pas exclure toi-même");
            con.getForWhom().sendRawMessage(messageManager.getMessage("manage_members.remove_member.cant_exclude_yourself"));
            return null;
        }else if (sqlManager.getMemberRank(answer) <= sqlManager.getMemberRank(authorName)){
            // con.getForWhom().sendRawMessage("Vous ne pouvez pas exclure un membre de rang supérieur à vous");
            con.getForWhom().sendRawMessage(messageManager.getMessage("manage_members.remove_member.cant_exclude_higher_rank"));
            return null;
        }
        
        plugin.addTeamTempAction(authorName, answer, teamId, "removeMember");
        Inventory inventory = inventoryBuilder.createConfirmInventory();
        author.openInventory(inventory);
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        // return "Envoie le pseudo du joueur à retirer";
        return messageManager.getMessage("manage_members.remove_member.send_player_name");
    }

}