package fr.army.stelyteam.events.inventoryclick;

import fr.army.stelyteam.StelyTeamPlugin;

import fr.army.stelyteam.utils.InventoryGenerator;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;


public class ManageInventory {
    private InventoryClickEvent event;

    public ManageInventory(InventoryClickEvent event) {
        this.event = event;
    }


    public void onInventoryClick(){
        Player player = (Player) event.getWhoClicked();
        String playerName = player.getName();
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
        Material itemType = event.getCurrentItem().getType();
        List<String> lore = event.getCurrentItem().getItemMeta().getLore();


        if (itemType.equals(Material.getMaterial(StelyTeamPlugin.config.getString("noPermission.itemType"))) && lore.equals(StelyTeamPlugin.config.getStringList("noPermission.lore"))){
            return;
        }
        
        // Liaisin des items avec leur fonction
        if (itemName.equals(StelyTeamPlugin.config.getString("inventories.manage.editMembers.itemName"))){
            Inventory inventory = InventoryGenerator.createEditMembersInventory(playerName);
            player.openInventory(inventory);

        }else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.manage.setTeamHome.itemName"))){
            player.closeInventory();
            String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromOwner(playerName);
            String worldName = player.getWorld().getName();
            Double x = player.getLocation().getX();
            Double y = player.getLocation().getY();
            Double z = player.getLocation().getZ();
            Double yaw = (double) player.getLocation().getYaw();

            if (StelyTeamPlugin.sqliteManager.isSet(teamID)){
                StelyTeamPlugin.sqliteManager.updateHome(teamID, worldName, x, y, z, yaw);
                player.sendMessage("Le home a été redéfini");

            }else{
                StelyTeamPlugin.sqliteManager.addHome(teamID, worldName, x, y, z, yaw);
                player.sendMessage("Le home a été créé");
            }
            
        }else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.manage.removeTeamHome.itemName"))){
            player.closeInventory();
            String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromOwner(playerName);

            if (StelyTeamPlugin.sqliteManager.isSet(teamID)){
                StelyTeamPlugin.sqliteManager.removeHome(teamID);
                player.sendMessage("Le home a été supprimé");
            }else{
                player.sendMessage("Le home n'a pas été défini");
            }
            
        }else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.manage.buyTeamBank.itemName"))){
            String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(playerName);
            
            if (!StelyTeamPlugin.sqlManager.hasUnlockedTeamBank(teamID)){
                StelyTeamPlugin.playersTempActions.put(playerName, "buyTeamBank");
                Inventory inventory = InventoryGenerator.createConfirmInventory();
                player.openInventory(inventory);
            }
        }else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.manage.upgradeTotalMembers.itemName"))){
            Inventory inventory = InventoryGenerator.createUpgradeTotalMembersInventory(playerName);
            player.openInventory(inventory);
        }else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.manage.editName.itemName"))){
            StelyTeamPlugin.playersTempActions.put(playerName, "editName");
            Inventory inventory = InventoryGenerator.createConfirmInventory();
            player.openInventory(inventory);
        }else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.manage.editPrefix.itemName"))){
            StelyTeamPlugin.playersTempActions.put(playerName, "editPrefix");
            Inventory inventory = InventoryGenerator.createConfirmInventory();
            player.openInventory(inventory);
        }else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.manage.removeTeam.itemName"))){
            StelyTeamPlugin.playersTempActions.put(playerName, "deleteTeam");
            Inventory inventory = InventoryGenerator.createConfirmInventory();
            player.openInventory(inventory);
        }else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.manage.editPermissions.itemName"))){
            Inventory inventory = InventoryGenerator.createPermissionsInventory(playerName);
            player.openInventory(inventory);


        // Fermeture ou retour en arrière de l'inventaire
        }else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.manage.close.itemName"))){
            Inventory inventory = InventoryGenerator.createAdminInventory();
            player.openInventory(inventory);
        }
    }
    

    public void getNameInput(Player player, Prompt prompt) {
        ConversationFactory cf = new ConversationFactory(StelyTeamPlugin.instance);
        Conversation conv = cf.withFirstPrompt(prompt).withLocalEcho(false).buildConversation(player);
        conv.begin();
        return;
    }
}
