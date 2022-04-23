package fr.army.stelyteam.conversations;

import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.InventoryGenerator;

public class ConvEditOwner extends StringPrompt {

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player player = Bukkit.getPlayer(answer);
        String playerName = player.getName();
        Player author = (Player) con.getForWhom();
        String authorName = author.getName();
        String teamId = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(author.getName());
        if (player == null) {
            con.getForWhom().sendRawMessage("Ce joueur n'existe pas");
            return null;
        }else if (!StelyTeamPlugin.sqlManager.isMemberInTeam(playerName, teamId)) {
            con.getForWhom().sendRawMessage("Ce joueur n'est pas dans ta team");
            return null;
        }

        StelyTeamPlugin.addTeamTempAction(authorName, answer, teamId, "editOwner");
        Inventory inventory = InventoryGenerator.createConfirmInventory();
        author.openInventory(inventory);
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        return "Envoie le pseudo du joueur à ajouter";
    }
}