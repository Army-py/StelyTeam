package fr.army.stelyteam.conversations;

import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.InventoryGenerator;
import fr.army.stelyteam.utils.MessageManager;

public class ConvEditOwner extends StringPrompt {

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player player = Bukkit.getPlayer(answer);
        Player author = (Player) con.getForWhom();
        String authorName = author.getName();
        String teamId = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(author.getName());
        
        if (player == null) {
            // con.getForWhom().sendRawMessage("Ce joueur n'existe pas");
            con.getForWhom().sendRawMessage(MessageManager.getMessage("common.player_not_exist"));
            return null;
        }else if (!StelyTeamPlugin.sqlManager.isMemberInTeam(answer, teamId)) {
            // con.getForWhom().sendRawMessage("Ce joueur n'est pas dans ta team");
            con.getForWhom().sendRawMessage(MessageManager.getMessage("common.player_not_in_your_team"));
            return null;
        }else if (StelyTeamPlugin.containTeamAction(answer, "editOwner")) {
            // con.getForWhom().sendRawMessage("Ce joueur a déjà une action en cours");
            con.getForWhom().sendRawMessage(MessageManager.getMessage("common.player_already_action"));
            return null;
        }

        StelyTeamPlugin.addTeamTempAction(authorName, answer, teamId, "editOwner");
        Inventory inventory = InventoryGenerator.createConfirmInventory();
        author.openInventory(inventory);
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        // return "Envoie le pseudo du joueur à ajouter";
        return MessageManager.getMessage("manage_members.edit_owner.send_player_name");
    }
}