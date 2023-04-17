package fr.army.stelyteam.events.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.army.stelyteam.conversations.ConvAddMember;
import fr.army.stelyteam.conversations.ConvEditOwner;
import fr.army.stelyteam.utils.Buttons;
import fr.army.stelyteam.utils.Menus;
import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.TeamMenu;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.builder.ItemBuilder;
import fr.army.stelyteam.utils.builder.conversation.ConversationBuilder;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;
import fr.army.stelyteam.utils.manager.database.SQLiteDataManager;


public class EditMembersMenu extends TeamMenu {

    final DatabaseManager mySqlManager = plugin.getDatabaseManager();
    final SQLiteDataManager sqliteManager = plugin.getSQLiteManager();
    final CacheManager cacheManager = plugin.getCacheManager();
    final MessageManager messageManager = plugin.getMessageManager();
    final ConversationBuilder conversationBuilder = plugin.getConversationBuilder();

    public EditMembersMenu(Player viewer){
        super(
            viewer,
            Menus.EDIT_MEMBERS_MENU.getName(),
            Menus.EDIT_MEMBERS_MENU.getSlots()
        );
    }


    public Inventory createInventory(Team team, String playerName) {
        Inventory inventory = Bukkit.createInventory(this, this.menuSlots, this.menuName);

        emptyCases(inventory, this.menuSlots, 0);
        Integer headSlot = 0;
        for(String memberName : team.getMembersName()){
            UUID playerUUID = sqliteManager.getUUID(memberName);
            String itemName;
            List<String> lore = new ArrayList<>();
            OfflinePlayer member;
            ItemStack item;

            if (playerUUID == null) member = Bukkit.getOfflinePlayer(memberName);
            else member = Bukkit.getOfflinePlayer(playerUUID);

            Integer memberRank = team.getMemberRank(memberName);
            String memberRankName = plugin.getRankFromId(memberRank);
            String rankColor = config.getString("ranks." + memberRankName + ".color");
            itemName = rankColor + memberName;
            
            if (!team.isTeamOwner(memberName)){
                lore = config.getStringList("editMembersLores");
                if (plugin.getLastRank() == memberRank) lore.remove(1);
            }

            lore.add(0, config.getString("prefixRankLore") + rankColor + config.getString("ranks." + memberRankName + ".name"));
            
            if (plugin.playerHasPermission(playerName, team, "manageMembers")){ 
                item = ItemBuilder.getPlayerHead(member, itemName, lore);
            }else{
                item = ItemBuilder.getItem(
                    Material.getMaterial(config.getString("noPermission.itemType")), 
                    itemName, 
                    config.getStringList("noPermission.lore"),
                    config.getString("noPermission.headTexture"),
                    false
                );
            }

            inventory.setItem(headSlot, item);
            headSlot ++;
        }

        for(String str : config.getConfigurationSection("inventories.editMembers").getKeys(false)){
            Integer slot = config.getInt("inventories.editMembers."+str+".slot");
            Material material = Material.getMaterial(config.getString("inventories.editMembers."+str+".itemType"));
            String name = config.getString("inventories.editMembers."+str+".itemName");
            String headTexture = config.getString("inventories.editMembers."+str+".headTexture");
            ItemStack item;

            if (plugin.playerHasPermission(playerName, team, str)){ 
                item = ItemBuilder.getItem(
                    material,
                    name,
                    config.getStringList("inventories.editMembers."+str+".lore"),
                    headTexture,
                    false);
            }else{
                item = ItemBuilder.getItem(
                    Material.getMaterial(config.getString("noPermission.itemType")), 
                    name, 
                    config.getStringList("noPermission.lore"),
                    config.getString("noPermission.headTexture"),
                    false
                );
            }

            inventory.setItem(slot, item);
        }
        return inventory;
    }


    public void openMenu(Team team){
        this.open(createInventory(team, viewer.getName()));
    }


    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        String itemName;
        Material itemType = clickEvent.getCurrentItem().getType();
        List<String> lore = clickEvent.getCurrentItem().getItemMeta().getLore();
        Player player = (Player) clickEvent.getWhoClicked();
        String playerName = player.getName();
        Team team = mySqlManager.getTeamFromPlayerName(playerName);


        if (itemType.equals(Material.getMaterial(config.getString("noPermission.itemType"))) && lore.equals(config.getStringList("noPermission.lore"))){
            return;
        }


        // Fermeture ou retour en arrière de l'inventaire
        itemName = clickEvent.getCurrentItem().getItemMeta().getDisplayName();
        if (Buttons.CLOSE_EDIT_MEMBERS_MENU_BUTTON.isClickedButton(clickEvent)){
            new ManageMenu(player).openMenu(team);
            return;
        }else if (Buttons.ADD_MEMBER_BUTTON.isClickedButton(clickEvent)){
            player.closeInventory();
            conversationBuilder.getNameInput(player, new ConvAddMember(plugin));
            return;
        }else if (Buttons.REMOVE_MEMBER_BUTTON.isClickedButton(clickEvent)){
            cacheManager.addTempAction(
                new TemporaryAction(playerName, TemporaryActionNames.CLICK_REMOVE_MEMBER, team)
            );
            new MembersMenu(player, Menus.REMOVE_MEMBERS_MENU.getName()).openMenu(team);
            return;
        }else if (Buttons.EDIT_OWNER_BUTTON.isClickedButton(clickEvent)){
            player.closeInventory();
            conversationBuilder.getNameInput(player, new ConvEditOwner(plugin));
            return;
        }

        itemName = removeFirstColors(clickEvent.getCurrentItem().getItemMeta().getDisplayName());
        if (team.isTeamMember(playerName)){
            Integer authorRank = team.getMemberRank(playerName);
            Integer memberRank = team.getMemberRank(itemName);
            Player member = Bukkit.getPlayer(itemName);

            if (clickEvent.getClick().isRightClick()){
                if (memberRank <= authorRank){
                    return;
                }
                if (!team.isTeamOwner(itemName) && memberRank < plugin.getLastRank()){
                    team.getMember(itemName).demoteMember();

                    if (member != null && removeFirstColors(itemName).equals(member.getName())){
                        String newRank = plugin.getRankFromId(memberRank+1);
                        String newRankName = config.getString("ranks." + newRank + ".name");
                        String newRankColor = config.getString("ranks." + newRank + ".color");
                        member.sendMessage(messageManager.getReplaceMessage("receiver.demote", newRankColor + newRankName));
                    }
                }
            }else if (clickEvent.getClick().isLeftClick()){
                if (memberRank-1 <= authorRank){
                    return;
                }
                if (!mySqlManager.isOwner(itemName) && memberRank != 1){
                    team.getMember(itemName).promoteMember();

                    if (member != null && removeFirstColors(itemName).equals(member.getName())){
                        String newRank = plugin.getRankFromId(memberRank-1);
                        String newRankName = config.getString("ranks." + newRank + ".name");
                        String newRankColor = config.getString("ranks." + newRank + ".color");
                        member.sendMessage(messageManager.getReplaceMessage("receiver.promote", newRankColor + newRankName));
                    }
                }
            }else return;

            new EditMembersMenu(player).openMenu(team);
            team.refreshTeamMembersInventory(playerName);
        }
    }


    @Override
    public void onClose(InventoryCloseEvent closeEvent) {}
}
