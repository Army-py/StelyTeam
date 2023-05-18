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
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.builder.ItemBuilder;
import fr.army.stelyteam.utils.manager.MessageManager;


public class UpgradeStorageMenu extends TeamMenu {

    final MessageManager messageManager = plugin.getMessageManager();

    public UpgradeStorageMenu(Player viewer){
        super(
            viewer,
            Menus.UPGRADE_LVL_STORAGE_MENU.getName(),
            Menus.UPGRADE_LVL_STORAGE_MENU.getSlots()
        );
    }


    public Inventory createInventory(Team team) {
        Integer level = team.getTeamStorageLvl();
        Inventory inventory = Bukkit.createInventory(this, this.menuSlots, this.menuName);
        Material material;
        String headTexture;

        emptyCases(inventory, this.menuSlots, 0);

        for(String str : config.getConfigurationSection("inventories.upgradeStorageAmount").getKeys(false)){
            Integer slot = config.getInt("inventories.upgradeStorageAmount."+str+".slot");
            String name = config.getString("inventories.upgradeStorageAmount."+str+".itemName");
            List<String> lore = config.getStringList("inventories.upgradeStorageAmount."+str+".lore");
            
            if (str.equals("close")){
                material = Material.getMaterial(config.getString("inventories.upgradeStorageAmount."+str+".itemType"));
                headTexture = config.getString("inventories.upgradeStorageAmount."+str+".headTexture");
            }else if (level >= config.getInt("inventories.upgradeStorageAmount."+str+".level")){
                material = Material.getMaterial(config.getString("inventories.upgradeStorageAmount."+str+".unlock.itemType"));
                headTexture = config.getString("inventories.upgradeStorageAmount."+str+".unlock.headTexture");
            }else{
                material = Material.getMaterial(config.getString("inventories.upgradeStorageAmount."+str+".lock.itemType"));
                headTexture = config.getString("inventories.upgradeStorageAmount."+str+".lock.headTexture");
            }

            inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, headTexture, false));
        }
        return inventory;
    }


    public void openMenu(Team team){
        this.open(createInventory(team));
    }


    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        Player player = (Player) clickEvent.getWhoClicked();
        String playerName = player.getName();
        String itemName = clickEvent.getCurrentItem().getItemMeta().getDisplayName();
        Material material = clickEvent.getCurrentItem().getType();

        Team team = plugin.getDatabaseManager().getTeamFromPlayerName(playerName);
        Integer level = team.getTeamStorageLvl();
        
        // Gestion des items
        if (Buttons.CLOSE_UPGRADE_LVL_STORAGE_MENU_BUTTON.isClickedButton(clickEvent)){
            new ManageMenu(player).openMenu(team);
        }else if (!material.name().equals(config.getString("emptyCase"))){
            for(String str : config.getConfigurationSection("inventories.upgradeStorageAmount").getKeys(false)){
                String name = config.getString("inventories.upgradeStorageAmount."+str+".itemName");
                
                if (itemName.equals(name) && level+1 == config.getInt("inventories.upgradeStorageAmount."+str+".level")){
                    if (plugin.getEconomyManager().checkMoneyPlayer(player, config.getDouble("prices.upgrade.teamStorages.level"+(level+1)))){
                        plugin.getCacheManager().addTempAction(new TemporaryAction(playerName, TemporaryActionNames.IMPROV_LVL_STORAGE, team));
                        new ConfirmMenu(player).openMenu();
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
