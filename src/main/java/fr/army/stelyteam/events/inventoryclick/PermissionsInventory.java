package fr.army.stelyteam.events.inventoryclick;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.InventoryGenerator;
import fr.army.stelyteam.utils.TeamMembersUtils;


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
            Integer permissionRank = StelyTeamPlugin.sqlManager.getPermissionRank(teamId, permission);
            Integer authorRank = StelyTeamPlugin.sqlManager.getMemberRank(playerName);
            boolean authorIsOwner = StelyTeamPlugin.sqlManager.isOwner(playerName);
            if (event.getClick().isRightClick()){
                if (permissionRank != null){
                    if (!authorIsOwner && permissionRank <= authorRank){
                        return;
                    }
                    if (permissionRank < StelyTeamPlugin.getLastRank()) StelyTeamPlugin.sqlManager.demoteRankPermission(teamId, permission);
                }else{
                    String rankPath = StelyTeamPlugin.config.getString("inventories.permissions."+permission+".rankPath");
                    Integer defaultRankId = StelyTeamPlugin.config.getInt("inventories."+rankPath+".rank");
                    if (!authorIsOwner && defaultRankId <= authorRank){
                        return;
                    }
                    if (defaultRankId != StelyTeamPlugin.getLastRank()) defaultRankId = defaultRankId+1;
                    StelyTeamPlugin.sqlManager.insertPermission(teamId, permission, defaultRankId);
                }
            }else if (event.getClick().isLeftClick()){
                if (permissionRank != null){
                    if (!authorIsOwner && permissionRank-1 <= authorRank){
                        return;
                    }
                    if (permissionRank > 0) StelyTeamPlugin.sqlManager.promoteRankPermission(teamId, permission);
                }else{
                    String rankPath = StelyTeamPlugin.config.getString("inventories.permissions."+permission+".rankPath");
                    Integer defaultRankId = StelyTeamPlugin.config.getInt("inventories."+rankPath+".rank");
                    if (!authorIsOwner && defaultRankId-1 <= authorRank){
                        return;
                    }
                    if (defaultRankId != 0) defaultRankId = defaultRankId-1;
                    StelyTeamPlugin.sqlManager.insertPermission(teamId, permission, defaultRankId);
                }
            }else return;
            Inventory inventory = InventoryGenerator.createPermissionsInventory(playerName);
            player.openInventory(inventory);
            TeamMembersUtils.refreshTeamMembersInventory(teamId, playerName);
        }
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
