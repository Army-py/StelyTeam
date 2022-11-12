package fr.army.stelyteam.utils;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.builder.InventoryBuilder;
import fr.army.stelyteam.utils.manager.SQLManager;

public class TeamMembersUtils {

    private StelyTeamPlugin plugin;
    private SQLManager sqlManager;
    private YamlConfiguration config;
    private InventoryBuilder inventoryBuilder;


    public TeamMembersUtils(StelyTeamPlugin plugin) {
        this.plugin = plugin;
        this.sqlManager = plugin.getSQLManager();
        this.config = plugin.getConfig();
        this.inventoryBuilder = new InventoryBuilder(plugin);
    }


    public void openMainInventory(Player player){
        String playerName = player.getName();
        String teamName = sqlManager.getTeamNameFromPlayerName(playerName);
        Inventory inventory;

        if (!sqlManager.isMember(playerName)){
            inventory = inventoryBuilder.createTeamInventory();
        }else if(sqlManager.isOwner(player.getName())){
            inventory = inventoryBuilder.createAdminInventory();
        }else if (plugin.playerHasPermissionInSection(playerName, teamName, "manage")){
            inventory = inventoryBuilder.createAdminInventory();
        }else inventory = inventoryBuilder.createMemberInventory(playerName);
        player.openInventory(inventory);
    }


    public void refreshTeamMembersInventory(String teamName, String authorName) {
        ArrayList<String> membersName = sqlManager.getTeamMembers(teamName);
        for (String memberName : membersName) {
            if (memberName.equals(authorName)) continue;

            HumanEntity player = Bukkit.getPlayer(memberName);
            if (player != null) {
                if (!config.getConfigurationSection("inventoriesName").getValues(true).containsValue(player.getOpenInventory().getTitle())){
                    continue;
                }

                if (player.getOpenInventory().getTitle().equals(config.getString("inventoriesName.admin"))){
                    player.openInventory(inventoryBuilder.createAdminInventory());
                }else if (player.getOpenInventory().getTitle().equals(config.getString("inventoriesName.manage"))){
                    player.openInventory(inventoryBuilder.createManageInventory(player.getName()));
                }else if (player.getOpenInventory().getTitle().equals(config.getString("inventoriesName.member"))){
                    player.openInventory(inventoryBuilder.createMemberInventory(player.getName()));
                }else if (player.getOpenInventory().getTitle().equals(config.getString("inventoriesName.upgradeTotalMembers"))){
                    player.openInventory(inventoryBuilder.createUpgradeTotalMembersInventory(player.getName()));
                }else if (player.getOpenInventory().getTitle().equals(config.getString("inventoriesName.editMembers"))){
                    player.openInventory(inventoryBuilder.createEditMembersInventory(player.getName()));
                }else if (player.getOpenInventory().getTitle().equals(config.getString("inventoriesName.teamMembers"))){
                    player.openInventory(inventoryBuilder.createMembersInventory(player.getName(), config.getString("inventoriesName.teamMembers")));
                }else if (player.getOpenInventory().getTitle().equals(config.getString("inventoriesName.permissions"))){
                    player.openInventory(inventoryBuilder.createPermissionsInventory(player.getName()));
                }else if (player.getOpenInventory().getTitle().equals(config.getString("inventoriesName.storageDirectory"))){
                    player.openInventory(inventoryBuilder.createStorageDirectoryInventory(player.getName()));
                }
            }
        }
    }


    public void closeTeamMembersInventory(String teamName, String authorName) {
        ArrayList<String> membersName = sqlManager.getTeamMembers(teamName);
        for (String memberName : membersName) {
            if (memberName.equals(authorName)) continue;

            HumanEntity player = Bukkit.getPlayer(memberName);
            if (player != null) {
                if (!config.getConfigurationSection("inventoriesName").getValues(true).containsValue(player.getOpenInventory().getTitle())){
                    continue;
                }

                player.closeInventory();
            }
        }
    }


    public void teamBroadcast(String teamName, String authorName, String message) {
        ArrayList<String> membersName = sqlManager.getTeamMembers(teamName);
        for (String memberName : membersName) {
            if (memberName.equals(authorName)) continue;

            HumanEntity player = Bukkit.getPlayer(memberName);
            if (player != null) {
                player.sendMessage(message);
            }
        }
    }
}
