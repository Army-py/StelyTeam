package fr.army.stelyteam.menu.impl;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.menu.Buttons;
import fr.army.stelyteam.menu.FixedMenu;
import fr.army.stelyteam.menu.Menus;
import fr.army.stelyteam.menu.TeamMenu;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.builder.ItemBuilder;
import fr.army.stelyteam.utils.manager.MessageManager;



public class UpgradeMembersMenu extends FixedMenu {

    final MessageManager messageManager = plugin.getMessageManager();

    public UpgradeMembersMenu(Player viewer, TeamMenu previousMenu) {
        super(
            viewer,
            Menus.UPGRADE_LVL_MEMBERS_MENU.getName(),
            Menus.UPGRADE_LVL_STORAGE_MENU.getSlots(),
            previousMenu
        );
    }


    public Inventory createInventory() {
        Integer level = team.getImprovLvlMembers();
        Inventory inventory = Bukkit.createInventory(this, this.menuSlots, this.menuName);
        Material material;
        String headTexture;

        emptyCases(inventory, this.menuSlots, 0);

        for(String buttonName : config.getConfigurationSection("inventories.upgradeTotalMembers").getKeys(false)){
            Integer slot = config.getInt("inventories.upgradeTotalMembers."+buttonName+".slot");
            String displayName = config.getString("inventories.upgradeTotalMembers."+buttonName+".itemName");
            List<String> lore = config.getStringList("inventories.upgradeTotalMembers."+buttonName+".lore");

            if (buttonName.equals("close")){
                material = Material.getMaterial(config.getString("inventories.upgradeTotalMembers."+buttonName+".itemType"));
                headTexture = config.getString("inventories.upgradeTotalMembers."+buttonName+".headTexture");
            }else if (level >= config.getInt("inventories.upgradeTotalMembers."+buttonName+".level")){
                material = Material.getMaterial(config.getString("inventories.upgradeTotalMembers."+buttonName+".unlock.itemType"));
                headTexture = config.getString("inventories.upgradeTotalMembers."+buttonName+".unlock.headTexture");
            }else{
                material = Material.getMaterial(config.getString("inventories.upgradeTotalMembers."+buttonName+".lock.itemType"));
                headTexture = config.getString("inventories.upgradeTotalMembers."+buttonName+".lock.headTexture");
            }
            inventory.setItem(slot, ItemBuilder.getItem(material, buttonName, displayName, lore, headTexture, false));
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
        Integer level = team.getImprovLvlMembers();
        
        if (Buttons.CLOSE_UPGRADE_LVL_MEMBERS_MENU_BUTTON.isClickedButton(clickEvent)){
            // new ManageMenu(player, this).openMenu();
            previousMenu.openMenu();
        }else if (!material.name().equals(config.getString("emptyCase"))){
            for(String str : config.getConfigurationSection("inventories.upgradeTotalMembers").getKeys(false)){
                String name = config.getString("inventories.upgradeTotalMembers."+str+".itemName");
                
                if (itemName.equals(name) && level+1 == config.getInt("inventories.upgradeTotalMembers."+str+".level")){
                    if (plugin.getEconomyManager().checkMoneyPlayer(player, config.getDouble("prices.upgrade.teamPlaces.level"+(level+1)))){
                        plugin.getCacheManager().addTempAction(new TemporaryAction(playerName, TemporaryActionNames.IMPROV_LVL_MEMBERS, team));
                        new ConfirmMenu(player, previousMenu).openMenu();
                        return;
                    }else{
                        player.sendMessage(messageManager.getMessage("common.not_enough_money"));
                        return;
                    }
                }else if (itemName.equals(name) && level >= config.getInt("inventories.upgradeTotalMembers."+str+".level")){
                    player.sendMessage(messageManager.getMessage("common.already_unlocked"));
                    return;
                }
            }
            player.sendMessage(messageManager.getMessage("manage_team.upgrade_member_amount.must_unlock_previous_level"));
        }
    }


    @Override
    public void onClose(InventoryCloseEvent closeEvent) {}
}
