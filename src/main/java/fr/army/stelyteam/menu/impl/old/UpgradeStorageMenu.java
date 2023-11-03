package fr.army.stelyteam.menu.impl.old;

import java.util.List;

import fr.army.stelyteam.menu.impl.old.ConfirmMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.menu.FixedMenuOLD;
import fr.army.stelyteam.menu.MenusOLD;
import fr.army.stelyteam.menu.TeamMenuOLD;
import fr.army.stelyteam.menu.button.Buttons;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.builder.ItemBuilderOLD;


public class UpgradeStorageMenu extends FixedMenuOLD {

    public UpgradeStorageMenu(Player viewer, TeamMenuOLD previousMenu) {
        super(
            viewer,
            MenusOLD.UPGRADE_LVL_STORAGE_MENU.getName(),
            MenusOLD.UPGRADE_LVL_STORAGE_MENU.getSlots(),
            previousMenu
        );
    }


    public Inventory createInventory() {
        Integer level = team.getTeamStorageLvl();
        Inventory inventory = Bukkit.createInventory(this, this.menuSlots, this.menuName);
        Material material;
        String headTexture;

        emptyCases(inventory, this.menuSlots, 0);

        for(String buttonName : config.getConfigurationSection("inventories.upgradeStorageAmount").getKeys(false)){
            Integer slot = config.getInt("inventories.upgradeStorageAmount."+buttonName+".slot");
            String displayName = config.getString("inventories.upgradeStorageAmount."+buttonName+".itemName");
            List<String> lore = config.getStringList("inventories.upgradeStorageAmount."+buttonName+".lore");
            
            if (buttonName.equals("close")){
                material = Material.getMaterial(config.getString("inventories.upgradeStorageAmount."+buttonName+".itemType"));
                headTexture = config.getString("inventories.upgradeStorageAmount."+buttonName+".headTexture");
            }else if (level >= config.getInt("inventories.upgradeStorageAmount."+buttonName+".level")){
                material = Material.getMaterial(config.getString("inventories.upgradeStorageAmount."+buttonName+".unlock.itemType"));
                headTexture = config.getString("inventories.upgradeStorageAmount."+buttonName+".unlock.headTexture");
            }else{
                material = Material.getMaterial(config.getString("inventories.upgradeStorageAmount."+buttonName+".lock.itemType"));
                headTexture = config.getString("inventories.upgradeStorageAmount."+buttonName+".lock.headTexture");
            }

            inventory.setItem(slot, ItemBuilderOLD.getItem(material, buttonName, displayName, lore, headTexture, false));
        }
        return inventory;
    }


    @Override
    public void openMenu(){
        this.open(createInventory());
    }


    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        Player player = (Player) clickEvent.getWhoClicked();
        String playerName = player.getName();
        String itemName = clickEvent.getCurrentItem().getItemMeta().getDisplayName();
        Material material = clickEvent.getCurrentItem().getType();

        Team team = Team.initFromPlayerName(playerName);
        Integer level = team.getTeamStorageLvl();
        
        // Gestion des items
        if (Buttons.CLOSE_UPGRADE_LVL_STORAGE_MENU_BUTTON.isClickedButton(clickEvent)){
            // new ManageMenu(player, this).openMenu();
            previousMenu.openMenu();
        }else if (!material.name().equals(config.getString("emptyCase"))){
            for(String str : config.getConfigurationSection("inventories.upgradeStorageAmount").getKeys(false)){
                String name = config.getString("inventories.upgradeStorageAmount."+str+".itemName");
                
                if (itemName.equals(name) && level+1 == config.getInt("inventories.upgradeStorageAmount."+str+".level")){
                    if (plugin.getEconomyManager().checkMoneyPlayer(player, config.getDouble("prices.upgrade.teamStorages.level"+(level+1)))){
                        plugin.getCacheManager().addTempAction(new TemporaryAction(playerName, TemporaryActionNames.IMPROV_LVL_STORAGE, team));
                        new ConfirmMenu(player, previousMenu).openMenu();
                        return;
                    }else{
                        player.sendMessage(messageManager.getMessage("common.not_enough_money"));
                        return;
                    }
                }else if (itemName.equals(name) && level >= config.getInt("inventories.upgradeStorageAmount."+str+".level")){
                    player.sendMessage(messageManager.getMessage("common.already_unlocked"));
                    return;
                }
            }
            player.sendMessage(messageManager.getMessage("manage_team.upgrade_storages.must_unlock_previous_level"));
        }
    }


    @Override
    public void onClose(InventoryCloseEvent closeEvent) {}
}
