package fr.army.stelyteam.conversations;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.InventoryGenerator;

public class ConvRemoveMember extends StringPrompt {

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        String authorName = author.getName();
        String teamId = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(author.getName());

        if (!StelyTeamPlugin.sqlManager.isMemberInTeam(answer, teamId)){
            con.getForWhom().sendRawMessage("Le joueur n'est pas dans ta team");
            return null;
        }else if (StelyTeamPlugin.containTeamAction(answer, "removeMember")) {
            con.getForWhom().sendRawMessage("Ce joueur a déjà une action en cours");
            return null;
        }
        
        StelyTeamPlugin.addTeamTempAction(authorName, answer, teamId, "removeMember");
        Inventory inventory = InventoryGenerator.createConfirmInventory();
        author.openInventory(inventory);
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        return "Envoie le pseudo du joueur à retirer";
    }

}