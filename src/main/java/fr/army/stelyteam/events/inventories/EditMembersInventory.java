package fr.army.stelyteam.events.inventories;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.conversations.ConvAddMember;
import fr.army.stelyteam.conversations.ConvEditOwner;
import fr.army.stelyteam.conversations.ConvRemoveMember;
import fr.army.stelyteam.utils.TeamMembersUtils;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.builder.InventoryBuilder;
import fr.army.stelyteam.utils.builder.conversation.ConversationBuilder;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.SQLManager;


public class EditMembersInventory {
    private InventoryClickEvent event;
    private StelyTeamPlugin plugin;
    private YamlConfiguration config;
    private CacheManager cacheManager;
    private SQLManager sqlManager;
    private MessageManager messageManager;
    private ConversationBuilder conversationBuilder;
    private InventoryBuilder inventoryBuilder;
    private TeamMembersUtils teamMembersUtils;


    public EditMembersInventory(InventoryClickEvent event, StelyTeamPlugin plugin) {
        this.event = event;
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.cacheManager = plugin.getCacheManager();
        this.sqlManager = plugin.getSQLManager();
        this.messageManager = plugin.getMessageManager();
        this.conversationBuilder = plugin.getConversationBuilder();
        this.inventoryBuilder = plugin.getInventoryBuilder();
        this.teamMembersUtils = plugin.getTeamMembersUtils();
    }


    public void onInventoryClick(){
        String itemName;
        Material itemType = event.getCurrentItem().getType();
        List<String> lore = event.getCurrentItem().getItemMeta().getLore();
        Player player = (Player) event.getWhoClicked();
        String playerName = player.getName();
        String teamId = sqlManager.getTeamNameFromPlayerName(playerName);
        


        if (itemType.equals(Material.getMaterial(config.getString("noPermission.itemType"))) && lore.equals(config.getStringList("noPermission.lore"))){
            return;
        }


        // Fermeture ou retour en arrière de l'inventaire
        itemName = event.getCurrentItem().getItemMeta().getDisplayName();
        if (itemName.equals(config.getString("inventories.editMembers.close.itemName"))){
            Inventory inventory = inventoryBuilder.createManageInventory(playerName);
            player.openInventory(inventory);
            return;
        }else if (itemName.equals(config.getString("inventories.editMembers.addMember.itemName"))){
            player.closeInventory();
            conversationBuilder.getNameInput(player, new ConvAddMember(plugin));
            return;
        }else if (itemName.equals(config.getString("inventories.editMembers.removeMember.itemName"))){
            // player.closeInventory();
            // conversationBuilder.getNameInput(player, new ConvRemoveMember(plugin));
            cacheManager.addTempAction(
                new TemporaryAction(playerName, TemporaryActionNames.CLICK_REMOVE_MEMBER)
            );
            player.openInventory(inventoryBuilder.createMembersInventory(playerName, config.getString("inventoriesName.removeMembers")));
            return;
        }else if (itemName.equals(config.getString("inventories.editMembers.editOwner.itemName"))){
            player.closeInventory();
            conversationBuilder.getNameInput(player, new ConvEditOwner(plugin));
            return;
        }

        itemName = removeFirstColors(event.getCurrentItem().getItemMeta().getDisplayName());
        if (sqlManager.getTeamMembers(teamId).contains(itemName)){
            Integer authorRank = sqlManager.getMemberRank(playerName);
            Integer memberRank = sqlManager.getMemberRank(itemName);
            Player member = Bukkit.getPlayer(itemName);

            if (event.getClick().isRightClick()){
                if (memberRank <= authorRank){
                    return;
                }
                if (!sqlManager.isOwner(itemName) && memberRank < plugin.getLastRank()){
                    sqlManager.demoteMember(teamId, itemName);

                    if (member != null && removeFirstColors(itemName).equals(member.getName())){
                        String newRank = plugin.getRankFromId(memberRank+1);
                        String newRankName = config.getString("ranks." + newRank + ".name");
                        String newRankColor = config.getString("ranks." + newRank + ".color");
                        // member.sendMessage("Vous avez été rétrogradé " + newRankColor + newRank);
                        member.sendMessage(messageManager.getReplaceMessage("receiver.demote", newRankColor + newRankName));
                    }
                }
            }else if (event.getClick().isLeftClick()){
                if (memberRank-1 <= authorRank){
                    return;
                }
                if (!sqlManager.isOwner(itemName) && memberRank != 1){
                    sqlManager.promoteMember(teamId, itemName);

                    if (member != null && removeFirstColors(itemName).equals(member.getName())){
                        String newRank = plugin.getRankFromId(memberRank-1);
                        String newRankName = config.getString("ranks." + newRank + ".name");
                        String newRankColor = config.getString("ranks." + newRank + ".color");
                        // member.sendMessage("Vous avez été promu " + newRankColor + newRank);
                        member.sendMessage(messageManager.getReplaceMessage("receiver.promote", newRankColor + newRankName));
                    }
                }
            }else return;
            Inventory inventory = inventoryBuilder.createEditMembersInventory(playerName);
            player.openInventory(inventory);
            teamMembersUtils.refreshTeamMembersInventory(teamId, playerName);
        }
    }


    private String removeFirstColors(String name){
        Pattern pattern = Pattern.compile("§.");
        Matcher matcher = pattern.matcher(name);
        int colors = 0;
        while (matcher.find()) {
            colors++;
        }
        return name.substring(name.length() - (name.length() - colors * pattern.pattern().length()));
    }
}
