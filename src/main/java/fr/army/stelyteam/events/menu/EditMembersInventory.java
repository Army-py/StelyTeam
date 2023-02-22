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


public class EditMembersInventory extends TeamMenu {

    final DatabaseManager mySqlManager = plugin.getDatabaseManager();
    final SQLiteDataManager sqliteManager = plugin.getSQLiteManager();
    final CacheManager cacheManager = plugin.getCacheManager();
    final MessageManager messageManager = plugin.getMessageManager();
    final ConversationBuilder conversationBuilder = plugin.getConversationBuilder();

    public EditMembersInventory(Player viewer){
        super(viewer);
    }


    public Inventory createInventory(Team team, String playerName) {
        Integer slots = config.getInt("inventoriesSlots.editMembers");
        Inventory inventory = Bukkit.createInventory(this, slots, config.getString("inventoriesName.editMembers"));

        emptyCases(inventory, slots, 0);
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
        if (itemName.equals(config.getString("inventories.editMembers.close.itemName"))){
            // Inventory inventory = inventoryBuilder.createManageInventory(playerName, team);
            // player.openInventory(inventory);
            new ManageInventory(player).openMenu(team);
            return;
        }else if (itemName.equals(config.getString("inventories.editMembers.addMember.itemName"))){
            player.closeInventory();
            conversationBuilder.getNameInput(player, new ConvAddMember(plugin));
            return;
        }else if (itemName.equals(config.getString("inventories.editMembers.removeMember.itemName"))){
            // player.closeInventory();
            // conversationBuilder.getNameInput(player, new ConvRemoveMember(plugin));
            cacheManager.addTempAction(
                new TemporaryAction(playerName, TemporaryActionNames.CLICK_REMOVE_MEMBER, team)
            );
            // player.openInventory(inventoryBuilder.createMembersInventory(team));
            new MembersInventory(player).openMenu(team);
            return;
        }else if (itemName.equals(config.getString("inventories.editMembers.editOwner.itemName"))){
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
                    team.getMember(playerName).demoteMember();

                    if (member != null && removeFirstColors(itemName).equals(member.getName())){
                        String newRank = plugin.getRankFromId(memberRank+1);
                        String newRankName = config.getString("ranks." + newRank + ".name");
                        String newRankColor = config.getString("ranks." + newRank + ".color");
                        // member.sendMessage("Vous avez été rétrogradé " + newRankColor + newRank);
                        member.sendMessage(messageManager.getReplaceMessage("receiver.demote", newRankColor + newRankName));
                    }
                }
            }else if (clickEvent.getClick().isLeftClick()){
                if (memberRank-1 <= authorRank){
                    return;
                }
                if (!mySqlManager.isOwner(itemName) && memberRank != 1){
                    team.getMember(playerName).promoteMember();

                    if (member != null && removeFirstColors(itemName).equals(member.getName())){
                        String newRank = plugin.getRankFromId(memberRank-1);
                        String newRankName = config.getString("ranks." + newRank + ".name");
                        String newRankColor = config.getString("ranks." + newRank + ".color");
                        // member.sendMessage("Vous avez été promu " + newRankColor + newRank);
                        member.sendMessage(messageManager.getReplaceMessage("receiver.promote", newRankColor + newRankName));
                    }
                }
            }else return;
            // Inventory inventory = inventoryBuilder.createEditMembersInventory(playerName, team);
            // player.openInventory(inventory);
            new EditMembersInventory(player).openMenu(team);
            team.refreshTeamMembersInventory(playerName);
        }
    }


    @Override
    public void onClose(InventoryCloseEvent closeEvent) {}


    private String removeFirstColors(String name){
        Pattern pattern = Pattern.compile("§.");
        Matcher matcher = pattern.matcher(name);
        int colors = 0;
        while (matcher.find()) {
            colors++;
        }
        return name.substring(name.length() - (name.length() - colors * pattern.pattern().length()));
    }
}
