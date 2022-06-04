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
        Player author = (Player) con.getForWhom();
        String authorName = author.getName();
        Player player = Bukkit.getPlayer(answer);
        String teamId = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(author.getName());
        
        if (player == null) {
            con.getForWhom().sendRawMessage("Ce joueur n'existe pas");
            return null;
        }else if (StelyTeamPlugin.sqlManager.isMember(answer)) {
            con.getForWhom().sendRawMessage("Ce joueur est déjà dans une team");
            return null;
        }else if (StelyTeamPlugin.containTeamAction(answer, "addMember")) {
            con.getForWhom().sendRawMessage("Ce joueur a déjà une action en cours");
            return null;
        }else if (hasReachedMaxMember(teamId)) {
            con.getForWhom().sendRawMessage("La team est déjà au maximum de membres");
            return null;
        }
        
        StelyTeamPlugin.addTeamTempAction(authorName, answer, teamId, "addMember");

        con.getForWhom().sendRawMessage("L'invitation a été envoyée");
        Inventory inventory = InventoryGenerator.createConfirmInventory();
        player.openInventory(inventory);

        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        return "Envoie le pseudo du joueur à ajouter";
    }


    private boolean hasReachedMaxMember(String teamId) {
        Integer memberAmount = StelyTeamPlugin.sqlManager.getMembers(teamId).size();
        Integer maxMember = StelyTeamPlugin.config.getInt("teamMaxMembers");
        Integer teamMembersLelvel = StelyTeamPlugin.sqlManager.getTeamLevel(teamId);
        return memberAmount >= maxMember + teamMembersLelvel;
    }

}