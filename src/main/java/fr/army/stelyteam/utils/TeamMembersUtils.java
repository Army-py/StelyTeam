package fr.army.stelyteam.utils;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;

import fr.army.stelyteam.StelyTeamPlugin;

public class TeamMembersUtils {

    private SQLManager sqlManager;
    private YamlConfiguration config;
    private InventoryBuilder inventoryBuilder;


    public TeamMembersUtils(StelyTeamPlugin plugin) {
        this.sqlManager = plugin.getSQLManager();
        this.config = plugin.getConfig();
        this.inventoryBuilder = plugin.getInventoryBuilder();
    }


    public void refreshTeamMembersInventory(String teamId, String authorName) {
        ArrayList<String> membersName = sqlManager.getMembers(teamId);
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
                    player.openInventory(inventoryBuilder.createMembersInventory(player.getName()));
                }else if (player.getOpenInventory().getTitle().equals(config.getString("inventoriesName.permissions"))){
                    player.openInventory(inventoryBuilder.createPermissionsInventory(player.getName()));
                }
            }
        }
    }


    public void closeTeamMembersInventory(String teamId, String authorName) {
        ArrayList<String> membersName = sqlManager.getMembers(teamId);
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


    public void teamBroadcast(String teamId, String authorName, String message) {
        ArrayList<String> membersName = sqlManager.getMembers(teamId);
        for (String memberName : membersName) {
            if (memberName.equals(authorName)) continue;

            HumanEntity player = Bukkit.getPlayer(memberName);
            if (player != null) {
                player.sendMessage(message);
            }
        }
    }
}
