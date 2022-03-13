package fr.army.stelyteam.conversations;

import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.InventoryGenerator;

public class ConvAddMember extends StringPrompt {

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player player = Bukkit.getPlayer(answer);
        if (player == null) {
            // author.sendMessage("Ce joueur n'existe pas");
            con.getForWhom().sendRawMessage("Ce joueur n'existe pas");
            return null;
        }
        
        if (StelyTeamPlugin.sqlManager.isMember(player.getName())) {
            con.getForWhom().sendRawMessage("Ce joueur est déjà dans une team");
            return null;
        }

        con.getForWhom().sendRawMessage("L'invitation a été envoyée");
        Inventory inventory = InventoryGenerator.createConfirmInventory();
        player.openInventory(inventory);
        StelyTeamPlugin.playersJoinTeam.add(player.getName());
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        return "Envoie le pseudo du joueur à ajouter";
    }

}