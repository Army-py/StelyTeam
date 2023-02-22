package fr.army.stelyteam.conversations;

import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.events.menu.ConfirmInventory;
import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.builder.InventoryBuilder;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;

public class ConvEditOwner extends StringPrompt {

    private CacheManager cacheManager;
    private DatabaseManager sqlManager;
    private MessageManager messageManager;
    private InventoryBuilder inventoryBuilder;

    public ConvEditOwner(StelyTeamPlugin plugin) {
        this.cacheManager = plugin.getCacheManager();
        this.sqlManager = plugin.getDatabaseManager();
        this.messageManager = plugin.getMessageManager();
        this.inventoryBuilder = plugin.getInventoryBuilder();
    }

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player player = Bukkit.getPlayer(answer);
        Player author = (Player) con.getForWhom();
        String authorName = author.getName();
        Team team = sqlManager.getTeamFromPlayerName(author.getName());
        
        if (player == null) {
            // con.getForWhom().sendRawMessage("Ce joueur n'existe pas");
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.player_not_exist"));
            return null;
        }else if (!team.isTeamMember(answer)) {
            // con.getForWhom().sendRawMessage("Ce joueur n'est pas dans ta team");
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.player_not_in_your_team"));
            return null;
        }else if (cacheManager.playerHasActionName(answer, TemporaryActionNames.EDIT_OWNER)) {
            // con.getForWhom().sendRawMessage("Ce joueur a déjà une action en cours");
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.player_already_action"));
            return null;
        }

        cacheManager.addTempAction(
            new TemporaryAction(
                authorName,
                answer, 
                TemporaryActionNames.EDIT_OWNER, 
                team)
        );
        // Inventory inventory = inventoryBuilder.createConfirmInventory();
        // author.openInventory(inventory);
        new ConfirmInventory(author).openMenu();
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        // return "Envoie le pseudo du joueur à ajouter";
        return messageManager.getMessage("manage_members.edit_owner.send_player_name");
    }
}