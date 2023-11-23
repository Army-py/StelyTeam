package fr.army.stelyteam.menu.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.menu.FixedMenuOLD;
import fr.army.stelyteam.menu.MenusOLD;
import fr.army.stelyteam.menu.TeamMenuOLD;
import fr.army.stelyteam.menu.button.Buttons;
import fr.army.stelyteam.team.Alliance;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.builder.ColorsBuilder;
import fr.army.stelyteam.utils.builder.ItemBuilderOLD;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;


public class AlliancesMenu extends FixedMenuOLD {

    private final DatabaseManager mySqlManager = plugin.getDatabaseManager();

    public AlliancesMenu(Player viewer, TeamMenuOLD previousMenu){
        super(
            viewer,
            MenusOLD.TEAM_ALLIANCES_MENU.getName(),
            MenusOLD.TEAM_ALLIANCES_MENU.getSlots(),
            previousMenu
        );
    }

    public AlliancesMenu(Player viewer, String menuName, TeamMenuOLD previousMenu){
        super(
            viewer,
            menuName,
            MenusOLD.TEAM_ALLIANCES_MENU.getSlots(),
            previousMenu
        );
    }


    public Inventory createInventory(String playerName) {
        final int maxMembersPerLine = config.getInt("maxMembersInLore");
        Inventory inventory = Bukkit.createInventory(this, this.menuSlots, this.menuName);

        emptyCases(inventory, this.menuSlots, 0);
        Integer headSlot = 0;
        for(Alliance alliance : team.getTeamAlliances()){
            Team teamAlliance = Team.init(alliance.getTeamUuid());
            String allianceName = teamAlliance.getTeamName();
            String alliancePrefix = teamAlliance.getTeamPrefix();
            String allianceOwnerName = teamAlliance.getTeamOwnerName();
            String allianceDate = alliance.getAllianceDate();
            Integer teamMembersLelvel = teamAlliance.getImprovLvlMembers();
            Integer teamMembers = teamAlliance.getTeamMembers().size();
            Integer maxMembers = config.getInt("teamMaxMembers");
            String allianceDescription = teamAlliance.getTeamDescription();
            List<String> allianceMembers = teamAlliance.getMembersName();
            allianceMembers.remove(allianceOwnerName);
            UUID playerUUID = mySqlManager.getUUID(allianceOwnerName);
            // String itemName = colorsBuilder.replaceColor(alliancePrefix);
            String displayName = " ";
            List<String> lore = config.getStringList("teamAllianceLore");
            // OfflinePlayer allianceOwner;
            ItemStack item;

            List<String> playerNames = new ArrayList<>();
            for(int i = 0; i < allianceMembers.size(); i++){
                if (i != 0 && i % maxMembersPerLine == 0) playerNames.add("%BACKTOLINE%");
                playerNames.add(allianceMembers.get(i));
            }
            
            lore = replaceInLore(lore, "%OWNER%", allianceOwnerName);
            lore = replaceInLore(lore, "%NAME%", allianceName);
            lore = replaceInLore(lore, "%PREFIX%", ColorsBuilder.replaceColor(alliancePrefix));
            lore = replaceInLore(lore, "%DATE%", allianceDate);
            lore = replaceInLore(lore, "%MEMBER_COUNT%", IntegerToString(teamMembers));
            lore = replaceInLore(lore, "%MAX_MEMBERS%", IntegerToString(maxMembers+teamMembersLelvel));
            lore = replaceInLore(lore, "%MEMBERS%", allianceMembers.isEmpty() ? messageManager.getMessageWithoutPrefix("common.no_members") : String.join(", ", playerNames));
            lore = replaceInLore(lore, "%DESCRIPTION%", ColorsBuilder.replaceColor(allianceDescription));
            
            
            if (plugin.playerHasPermission(playerName, team, "seeTeamAlliances")){ 
                item = ItemBuilderOLD.getPlayerHead(playerUUID, displayName, lore);
            }else{
                item = ItemBuilderOLD.getItem(
                    Material.getMaterial(config.getString("noPermission.itemType")),
                    "noPermission",
                    displayName, 
                    config.getStringList("noPermission.lore"),
                    config.getString("noPermission.headTexture"),
                    false
                );
            }

            inventory.setItem(headSlot, item);
            headSlot ++;
        }

        for(String buttonName : config.getConfigurationSection("inventories.teamAlliances").getKeys(false)){
            Integer slot = config.getInt("inventories.teamAlliances."+buttonName+".slot");
            Material material = Material.getMaterial(config.getString("inventories.teamAlliances."+buttonName+".itemType"));
            String displayName = config.getString("inventories.teamAlliances."+buttonName+".itemName");
            String headTexture = config.getString("inventories.teamAlliances."+buttonName+".headTexture");
            
            inventory.setItem(slot, ItemBuilderOLD.getItem(material, buttonName, displayName, Collections.emptyList(), headTexture, false));
        }
        return inventory;
    }


    @Override
    public void openMenu(){
        this.open(createInventory(viewer.getName()));
    }


    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        Player player = (Player) clickEvent.getWhoClicked();
        String playerName = player.getName();
        Material material = clickEvent.getCurrentItem().getType();

        if (clickEvent.getView().getTitle().equals(MenusOLD.REMOVE_ALLIANCES_MENU.getName())){
            if (material.equals(Material.getMaterial("PLAYER_HEAD"))){
                if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.CLICK_REMOVE_ALLIANCE)){
                    NamespacedKey key = new NamespacedKey(StelyTeamPlugin.getPlugin(), "uuid");
                    ItemMeta meta = clickEvent.getCurrentItem().getItemMeta();
                    PersistentDataContainer container = meta.getPersistentDataContainer();
                    long[] ownerUuid = container.get(key, PersistentDataType.LONG_ARRAY);
                    Team alliance = Team.initFromPlayerUuid(new UUID(ownerUuid[0], ownerUuid[1]));

                    if (alliance == null) return;

                    UUID allianceUuid = alliance.getTeamUuid();

                    if (!team.isTeamAlliance(allianceUuid)){
                        player.sendRawMessage(messageManager.getMessage("common.not_in_alliance"));
                        return;
                    }

                    cacheManager.removePlayerAction(playerName);
                    cacheManager.addTempAction(
                        new TemporaryAction(
                                playerName,
                                allianceUuid.toString(),
                                TemporaryActionNames.REMOVE_ALLIANCE,
                                team
                            )
                        );
                    new ConfirmMenu(player, this).openMenu();
                }
            }else if (Buttons.CLOSE_TEAM_ALLIANCES_MENU_BUTTON.isClickedButton(clickEvent)){
                // new EditAlliancesMenu(player, previousMenu).openMenu();
                previousMenu.openMenu();
            }

        }else{
            if (Buttons.CLOSE_TEAM_ALLIANCES_MENU_BUTTON.isClickedButton(clickEvent)){
                // new MemberMenu(player, this).openMenu();
                previousMenu.openMenu();
            }
        }
    }

    @Override
    public void onClose(InventoryCloseEvent closeEvent) {}
}
