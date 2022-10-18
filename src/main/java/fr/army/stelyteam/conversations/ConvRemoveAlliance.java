package fr.army.stelyteam.conversations;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.InventoryBuilder;
import fr.army.stelyteam.utils.MessageManager;
import fr.army.stelyteam.utils.SQLManager;

public class ConvRemoveAlliance extends StringPrompt {

    private StelyTeamPlugin plugin;
    private SQLManager sqlManager;
    private MessageManager messageManager;
    private InventoryBuilder inventoryBuilder;


    public ConvRemoveAlliance(StelyTeamPlugin plugin) {
        this.plugin = plugin;
        this.sqlManager = plugin.getSQLManager();
        this.messageManager = plugin.getMessageManager();
        this.inventoryBuilder = plugin.getInventoryBuilder();
    }


    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        String authorName = author.getName();
        String teamName = sqlManager.getTeamNameFromPlayerName(author.getName());

        if (!sqlManager.isAlliance(answer, teamName)){
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.not_in_alliance"));
            return null;
        }
        
        plugin.addTeamTempAction(authorName, answer, teamName, "removeAlliance");
        Inventory inventory = inventoryBuilder.createConfirmInventory();
        author.openInventory(inventory);
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        // return "Envoie le pseudo du joueur à retirer";
        return messageManager.getMessage("manage_alliances.remove_alliance.send_team_name");
    }

}