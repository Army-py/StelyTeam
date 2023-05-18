package fr.army.stelyteam.menu;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.utils.Buttons;
import fr.army.stelyteam.utils.Menus;
import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.TeamMenu;
import fr.army.stelyteam.utils.builder.ItemBuilder;


public class PermissionsMenu extends TeamMenu {

    public PermissionsMenu(Player viewer) {
        super(
            viewer,
            Menus.PERMISSIONS_MENU.getName(),
            Menus.PERMISSIONS_MENU.getSlots()
        );
    }


    public Inventory createInventory(Team team) {
        Inventory inventory = Bukkit.createInventory(this, this.menuSlots, this.menuName);

        emptyCases(inventory, this.menuSlots, 0);

        for(String str : config.getConfigurationSection("inventories.permissions").getKeys(false)){
            Integer slot = config.getInt("inventories.permissions."+str+".slot");
            if (slot == -1) continue;

            String name = config.getString("inventories.permissions."+str+".itemName");
            List<String> lore = config.getStringList("inventories.permissions."+str+".lore");
            String headTexture;
            
            Integer defaultRankId = config.getInt("inventories.permissions."+str+".rank");
            Integer permissionRank = team.getPermissionRank(str);
            String lorePrefix = config.getString("prefixRankLore");
            Material material;

            if (str.equals("close")){
                material = Material.getMaterial(config.getString("inventories.permissions."+str+".itemType"));
                headTexture = config.getString("inventories.permissions."+str+".headTexture");
            }else{
                material = Material.getMaterial(config.getString("ranks."+plugin.getRankFromId(permissionRank != null ? permissionRank : defaultRankId)+".itemType"));
                headTexture = config.getString("ranks."+plugin.getRankFromId(permissionRank != null ? permissionRank : defaultRankId)+".headTexture");
                
                if (permissionRank != null){
                    String rankColor = config.getString("ranks." + plugin.getRankFromId(permissionRank) + ".color");
                    lore.add(0, lorePrefix + rankColor + config.getString("ranks." + plugin.getRankFromId(permissionRank) + ".name"));
                }else{
                    String rankColor = config.getString("ranks." + plugin.getRankFromId(defaultRankId) + ".color");
                    lore.add(0, lorePrefix + rankColor + config.getString("ranks." + plugin.getRankFromId(defaultRankId) + ".name"));
                }
            }


            boolean isDefault = false;
            if (!str.equals("close") && (permissionRank == null || defaultRankId == permissionRank)){
                isDefault = true;
            }

            inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, headTexture, isDefault));
        }
        return inventory;
    }


    public void openMenu(Team team) {
        this.open(createInventory(team));
    }


    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        Player player = (Player) clickEvent.getWhoClicked();
        String playerName = player.getName();
        String itemName = clickEvent.getCurrentItem().getItemMeta().getDisplayName();
        Team team = plugin.getDatabaseManager().getTeamFromPlayerName(playerName);

        // Fermeture ou retour en arrière de l'inventaire
        if (Buttons.CLOSE_PERMISSIONS_MENU_BUTTON.isClickedButton(clickEvent)){
            new ManageMenu(player).openMenu(team);
            return;
        }

        if (getPermissionFromName(itemName) != null){
            String permission = getPermissionFromName(itemName);
            Integer permissionRank = team.getPermissionRank(permission);
            Integer authorRank = team.getMemberRank(playerName);
            boolean authorIsOwner = team.isTeamOwner(playerName);
            if (clickEvent.getClick().isRightClick()){
                if (permissionRank != null){
                    if (!authorIsOwner && permissionRank <= authorRank){
                        return;
                    }
                    if (permissionRank < plugin.getLastRank()) team.incrementAssignement(permission);
                }else{
                    Integer defaultRankId = config.getInt("inventories.permissions."+permission+".rank");
                    if (!authorIsOwner && defaultRankId <= authorRank){
                        return;
                    }
                    if (defaultRankId != plugin.getLastRank()) defaultRankId = defaultRankId+1;
                    team.insertAssignement(permission, defaultRankId);
                }
            }else if (clickEvent.getClick().isLeftClick()){
                if (permissionRank != null){
                    if (!authorIsOwner && permissionRank-1 <= authorRank){
                        return;
                    }
                    if (permissionRank > 0) team.decrementAssignement(permission);
                }else{
                    Integer defaultRankId = config.getInt("inventories.permissions."+permission+".rank");
                    if (!authorIsOwner && defaultRankId-1 <= authorRank){
                        return;
                    }
                    if (defaultRankId != 0) defaultRankId = defaultRankId-1;
                    team.insertAssignement(permission, defaultRankId);
                }
            }else return;
            new PermissionsMenu(player).openMenu(team);
            team.refreshTeamMembersInventory(playerName);
        }
    }


    @Override
    public void onClose(InventoryCloseEvent closeEvent) {}



    private String getPermissionFromName(String value) {
        for (String key : config.getConfigurationSection("inventories.permissions").getKeys(false)) {
            if (config.getString("inventories.permissions." + key + ".itemName").equals(value)) {
                return key;
            }
        }
        return null;
    }
}
