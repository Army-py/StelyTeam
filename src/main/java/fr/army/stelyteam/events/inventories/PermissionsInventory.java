package fr.army.stelyteam.events.inventories;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.builder.InventoryBuilder;
import fr.army.stelyteam.utils.manager.MySQLManager;


public class PermissionsInventory {

    private InventoryClickEvent event;
    private StelyTeamPlugin plugin;
    private YamlConfiguration config;
    private MySQLManager sqlManager;
    private InventoryBuilder inventoryBuilder;


    public PermissionsInventory(InventoryClickEvent event, StelyTeamPlugin plugin){
        this.event = event;
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.sqlManager = plugin.getSQLManager();
        this.inventoryBuilder = plugin.getInventoryBuilder();
    }


    public void onInventoryClick(){
        Player player = (Player) event.getWhoClicked();
        String playerName = player.getName();
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
        Team team = sqlManager.getTeamFromPlayerName(playerName);

        // Fermeture ou retour en arrière de l'inventaire
        if (itemName.equals(config.getString("inventories.permissions.close.itemName"))){
            Inventory inventory = inventoryBuilder.createManageInventory(playerName, team);
            player.openInventory(inventory);
            return;
        }

        if (getPermissionFromName(itemName) != null){
            String permission = getPermissionFromName(itemName);
            Integer permissionRank = team.getPermissionRank(permission);
            Integer authorRank = team.getMemberRank(playerName);
            boolean authorIsOwner = team.isTeamOwner(playerName);
            if (event.getClick().isRightClick()){
                if (permissionRank != null){
                    if (!authorIsOwner && permissionRank <= authorRank){
                        return;
                    }
                    if (permissionRank < plugin.getLastRank()) team.incrementAssignement(permission);
                }else{
                    String rankPath = config.getString("inventories.permissions."+permission+".rankPath");
                    Integer defaultRankId = config.getInt("inventories."+rankPath+".rank");
                    if (!authorIsOwner && defaultRankId <= authorRank){
                        return;
                    }
                    if (defaultRankId != plugin.getLastRank()) defaultRankId = defaultRankId+1;
                    team.insertAssignement(permission, defaultRankId);
                }
            }else if (event.getClick().isLeftClick()){
                if (permissionRank != null){
                    if (!authorIsOwner && permissionRank-1 <= authorRank){
                        return;
                    }
                    if (permissionRank > 0) team.decrementAssignement(permission);
                }else{
                    String rankPath = config.getString("inventories.permissions."+permission+".rankPath");
                    Integer defaultRankId = config.getInt("inventories."+rankPath+".rank");
                    if (!authorIsOwner && defaultRankId-1 <= authorRank){
                        return;
                    }
                    if (defaultRankId != 0) defaultRankId = defaultRankId-1;
                    team.insertAssignement(permission, defaultRankId);
                }
            }else return;
            Inventory inventory = inventoryBuilder.createPermissionsInventory(playerName, team);
            player.openInventory(inventory);
            team.refreshTeamMembersInventory(playerName);
        }
    }


    public String getPermissionFromName(String value) {
        for (String key : config.getConfigurationSection("inventories.permissions").getKeys(false)) {
            if (config.getString("inventories.permissions." + key + ".itemName").equals(value)) {
                return key;
            }
        }
        return null;
    }
}
