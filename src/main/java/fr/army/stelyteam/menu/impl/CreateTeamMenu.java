package fr.army.stelyteam.menu.impl;

import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.menu.FixedMenuOLD;
import fr.army.stelyteam.menu.MenusOLD;
import fr.army.stelyteam.menu.TeamMenu;
import fr.army.stelyteam.menu.TeamMenuOLD;
import fr.army.stelyteam.menu.button.Button;
import fr.army.stelyteam.menu.button.Buttons;
import fr.army.stelyteam.menu.button.container.ButtonContainer;
import fr.army.stelyteam.menu.template.MenuTemplate;
import fr.army.stelyteam.menu.view.MenuView;
import fr.army.stelyteam.menu.view.TeamMenuView;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.builder.ItemBuilder;
import fr.army.stelyteam.utils.builder.menu.MenuBuilder;
import fr.army.stelyteam.utils.builder.menu.MenuBuilderResult;


public class CreateTeamMenu extends TeamMenu<TeamMenuView> {

    private CreateTeamMenu(MenuBuilderResult menuBuilderResult) {
        super(menuBuilderResult);
    }


    @Override
    public TeamMenuView createView(Player player, Optional<Team> team) {
        return new TeamMenuView(player, this, team.orElse(null));
    }

    public static CreateTeamMenu createInstance(String configName) {
        final MenuBuilderResult builderResult = MenuBuilder.getInstance().loadMenu(configName + ".yml");
        final MenuTemplate menuTemplate = builderResult.getMenuTemplate();
        final YamlConfiguration config = builderResult.getConfig();

        for (String section : config.getConfigurationSection("items").getKeys(false)) {
            final ConfigurationSection itemSection = config.getConfigurationSection("items." + section);
            final Buttons buttonType = Buttons.getButtonType(itemSection.getString("button-type"));
            
            final Button<ButtonContainer<Team>> button;
            switch (buttonType) {
                case BUTTON_CREATE_TEAM:
                    break;
            
                default:
                    break;
            }

            if (itemSection.getString("button-type").equals(Buttons.BUTTON_CREATE_TEAM.toString())) {
                ConfigurationSection sellSection = itemSection.getConfigurationSection("sell");

                final Material material = Material.getMaterial(sellSection.getString("material"));
                final String name = sellSection.getString("name");
                final String skullTexture = sellSection.getString("skull-texture") == null
                        || sellSection.getString("skull-texture").isEmpty() ? null
                                : sellSection.getString("skull-texture");
                final int price = sellSection.getInt("price");
                final int quantity = sellSection.getInt("quantity");

                for (int slot : menu.getSlots(section)) {
                    final SellingButton button = SellingButton.mapButton(menu.getButton(slot));
                    button.setSellingMaterial(material);
                    button.setSellingItemName(name);
                    button.setSellingSkullTexture(skullTexture);
                    button.setSellingPrice(price);
                    button.setSellingQuantity(quantity);
                    button.setNPCName(npcName);
                    menu.mapButton(slot, button);
                }
            }else if (itemSection.getString("button-type").equals(ButtonType.BUYING.toString())) {
                ConfigurationSection buyingSection = itemSection.getConfigurationSection("buy");

                final Material material = Material.getMaterial(buyingSection.getString("material"));
                final String name = buyingSection.getString("name");
                final String skullTexture = buyingSection.getString("skull-texture") == null
                        || buyingSection.getString("skull-texture").isEmpty() ? null
                                : buyingSection.getString("skull-texture");
                final int price = buyingSection.getInt("price");
                final int quantity = buyingSection.getInt("quantity");

                for (int slot : menu.getSlots(section)) {
                    // final BuyingButton button = BuyingButton.mapButton(menu.getButton(slot))
                    //         .setBuyingMaterial(material)
                    //         .setBuyingItemName(name)
                    //         .setBuyingSkullTexture(skullTexture)
                    //         .setBuyingPrice(price)
                    //         .setBuyingQuantity(quantity)
                    //         .setNPCName(npcName);
                    final BuyingButton.Builder button = new BuyingButton.Builder();
                    menu.mapButton(slot, button);
                }
            } else if (itemSection.getString("button-type").equals(ButtonType.BACK.toString())) {
                for (int slot : menu.getSlots(section)) {
                    final BackButton button = BackButton.mapButton(menu.getButton(slot));
                    menu.setButton(slot, button);
                }
            }
        }

        return new CreateTeamMenu(builderResult);
    }


    public Inventory createInventory() {
		Inventory inventory = Bukkit.createInventory(this, this.menuSlots, this.menuName);
		
        emptyCases(inventory, this.menuSlots, 0);

        for(String buttonName : config.getConfigurationSection("inventories.createTeam").getKeys(false)){
            Integer slot = config.getInt("inventories.createTeam."+buttonName+".slot");
            Material material = Material.getMaterial(config.getString("inventories.createTeam."+buttonName+".itemType"));
            String displayName = config.getString("inventories.createTeam."+buttonName+".itemName");
            List<String> lore = config.getStringList("inventories.createTeam."+buttonName+".lore");
            String headTexture = config.getString("inventories.createTeam."+buttonName+".headTexture");
            inventory.setItem(slot, ItemBuilder.getItem(material, buttonName, displayName, lore, headTexture, false));
        }
		
		return inventory;
    }


    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        Player player = (Player) clickEvent.getWhoClicked();
        String playerName = player.getName();

        if (Buttons.CREATE_TEAM_BUTTON.isClickedButton(clickEvent)){
            if (plugin.getEconomyManager().checkMoneyPlayer(player, config.getDouble("prices.createTeam"))){
                new ConfirmMenu(viewer, this).openMenu();
                plugin.getCacheManager().addTempAction(
                    new TemporaryAction(
                        playerName,
                        TemporaryActionNames.CREATE_TEAM,
                        Team.initFromPlayerName(playerName)));
            }else{
                player.sendMessage(plugin.getMessageManager().getMessage("common.not_enough_money"));
            }
        }else if (Buttons.TEAM_LIST_MENU_BUTTON.isClickedButton(clickEvent)){
            new TeamListMenu(viewer, this).openMenu(0);
        }
    }
}
