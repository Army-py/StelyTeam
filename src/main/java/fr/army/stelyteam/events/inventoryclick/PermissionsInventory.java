package fr.army.stelyteam.events.inventoryclick;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.InventoryGenerator;


public class PermissionsInventory {
    private InventoryClickEvent event;

    public PermissionsInventory(InventoryClickEvent event) {
        this.event = event;
    }

    public void onInventoryClick(){
        Player player = (Player) event.getWhoClicked();
        String playerName = player.getName();
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
        String teamId = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(playerName);

        // Fermeture ou retour en arrière de l'inventaire
        if (itemName.equals(StelyTeamPlugin.config.getString("inventories.permissions.close.itemName"))){
            Inventory inventory = InventoryGenerator.createManageInventory(playerName);
            player.openInventory(inventory);
            return;
        }

        if (getPermissionFromName(itemName) != null){
            String permission = getPermissionFromName(itemName);
            if (event.getClick().isLeftClick()){
                if (StelyTeamPlugin.sqlManager.getPermissionRank(teamId, permission) != null){
                    Integer permissionRank = StelyTeamPlugin.sqlManager.getPermissionRank(teamId, permission);
                    if (permissionRank > 0) StelyTeamPlugin.sqlManager.demoteRankPermission(teamId, permission);
                }else{
                    String rankPath = StelyTeamPlugin.config.getString("inventories.permissions."+permission+".rankPath");
                    Integer defaultRankId = StelyTeamPlugin.config.getInt("inventories."+rankPath+".rank");
                    StelyTeamPlugin.sqlManager.insertPermission(teamId, permission, defaultRankId-1);
                }
            }else if (event.getClick().isRightClick()){
                if (StelyTeamPlugin.sqlManager.getPermissionRank(teamId, permission) != null){
                    Integer permissionRank = StelyTeamPlugin.sqlManager.getPermissionRank(teamId, permission);
                    if (permissionRank < StelyTeamPlugin.getLastRank()) StelyTeamPlugin.sqlManager.promoteRankPermission(teamId, permission);
                }else{
                    String rankPath = StelyTeamPlugin.config.getString("inventories.permissions."+permission+".rankPath");
                    Integer defaultRankId = StelyTeamPlugin.config.getInt("inventories."+rankPath+".rank");
                    StelyTeamPlugin.sqlManager.insertPermission(teamId, permission, defaultRankId+1);
                }
            }
            Inventory inventory = InventoryGenerator.createPermissionsInventory(playerName);
            player.openInventory(inventory);
        }
        // if (event.getClick().isRightClick()){
        //     if (!StelyTeamPlugin.sqlManager.isOwner(itemName) && StelyTeamPlugin.sqlManager.getMemberRank(itemName) < StelyTeamPlugin.getLastRank()){
        //         StelyTeamPlugin.sqlManager.demoteMember(teamId, itemName);
        //     }
        // }
        // if (event.getClick().isLeftClick()){
        //     if (!StelyTeamPlugin.sqlManager.isOwner(itemName) && StelyTeamPlugin.sqlManager.getMemberRank(itemName) != 1){
        //         StelyTeamPlugin.sqlManager.promoteMember(teamId, itemName);
        //     }
        // }
        // Inventory inventory = InventoryGenerator.createEditMembersInventory(playerName);
        // player.openInventory(inventory);
    }


    public static String getPermissionFromName(String value) {
        for (String key : StelyTeamPlugin.config.getConfigurationSection("inventories.permissions").getKeys(false)) {
            if (StelyTeamPlugin.config.getString("inventories.permissions." + key + ".itemName").equals(value)) {
                return key;
            }
        }
        return null;
    }
}
