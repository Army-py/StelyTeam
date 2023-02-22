package fr.army.stelyteam.conversations;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.events.inventories.ConfirmInventory;
import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.builder.InventoryBuilder;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;

public class ConvRemoveAlliance extends StringPrompt {

    private CacheManager cacheManager;
    private DatabaseManager sqlManager;
    private MessageManager messageManager;
    private InventoryBuilder inventoryBuilder;


    public ConvRemoveAlliance(StelyTeamPlugin plugin) {
        this.cacheManager = plugin.getCacheManager();
        this.sqlManager = plugin.getDatabaseManager();
        this.messageManager = plugin.getMessageManager();
        this.inventoryBuilder = plugin.getInventoryBuilder();
    }


    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        String authorName = author.getName();
        Team team = sqlManager.getTeamFromPlayerName(authorName);

        if (!team.isTeamAlliance(answer)){
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.not_in_alliance"));
            return null;
        }
        
        cacheManager.addTempAction(
            new TemporaryAction(
                authorName,
                answer,
                TemporaryActionNames.REMOVE_ALLIANCE,
                team)
        );
        // Inventory inventory = inventoryBuilder.createConfirmInventory();
        // author.openInventory(inventory);
        new ConfirmInventory(author).openMenu();
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        // return "Envoie le pseudo du joueur à retirer";
        return messageManager.getMessage("manage_alliances.remove_alliance.send_team_name");
    }

}