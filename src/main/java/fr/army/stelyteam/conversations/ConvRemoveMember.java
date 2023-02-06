package fr.army.stelyteam.conversations;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.builder.InventoryBuilder;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.MySQLManager;

public class ConvRemoveMember extends StringPrompt {

    private StelyTeamPlugin plugin;
    private CacheManager cacheManager;
    private MySQLManager sqlManager;
    private MessageManager messageManager;
    private InventoryBuilder inventoryBuilder;


    public ConvRemoveMember(StelyTeamPlugin plugin) {
        this.plugin = plugin;
        this.cacheManager = plugin.getCacheManager();
        this.sqlManager = plugin.getSQLManager();
        this.messageManager = plugin.getMessageManager();
        this.inventoryBuilder = plugin.getInventoryBuilder();
    }


    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        String authorName = author.getName();
        Team team = sqlManager.getTeamFromPlayerName(author.getName());

        if (!team.isTeamMember(answer)){
            // con.getForWhom().sendRawMessage("Le joueur n'est pas dans ta team");
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.player_not_in_your_team"));
            return null;
        }else if (authorName.equals(answer)){
            // con.getForWhom().sendRawMessage("Tu ne peux pas exclure toi-même");
            con.getForWhom().sendRawMessage(messageManager.getMessage("manage_members.remove_member.cant_exclude_yourself"));
            return null;
        }else if (team.getMemberRank(answer) <= team.getMemberRank(authorName)){
            // con.getForWhom().sendRawMessage("Vous ne pouvez pas exclure un membre de rang supérieur à vous");
            con.getForWhom().sendRawMessage(messageManager.getMessage("manage_members.remove_member.cant_exclude_higher_rank"));
            return null;
        }
        
        cacheManager.addTempAction(
            new TemporaryAction(
                authorName,
                answer,
                TemporaryActionNames.REMOVE_MEMBER,
                team)
        );
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