package fr.army.stelyteam.utils;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;

import fr.army.stelyteam.StelyTeamPlugin;

public class RefreshPlayersInventory {
    public static void refreshTeamMembersInventory(String teamId, String authorName) {
        ArrayList<String> membersName = StelyTeamPlugin.sqlManager.getMembers(teamId);
        for (String memberName : membersName) {
            if (memberName.equals(authorName)) continue;

            HumanEntity player = Bukkit.getPlayer(memberName);
            if (player != null) {
                if (!StelyTeamPlugin.config.getConfigurationSection("inventoriesName").getValues(true).containsValue(player.getOpenInventory().getTitle())){
                    continue;
                }

                if (player.getOpenInventory().getTitle().equals(StelyTeamPlugin.config.getString("inventoriesName.admin"))){
                    player.openInventory(InventoryGenerator.createAdminInventory());
                }else if (player.getOpenInventory().getTitle().equals(StelyTeamPlugin.config.getString("inventoriesName.manage"))){
                    player.openInventory(InventoryGenerator.createManageInventory(player.getName()));
                }else if (player.getOpenInventory().getTitle().equals(StelyTeamPlugin.config.getString("inventoriesName.member"))){
                    player.openInventory(InventoryGenerator.createMemberInventory(player.getName()));
                }else if (player.getOpenInventory().getTitle().equals(StelyTeamPlugin.config.getString("inventoriesName.upgradeTotalMembers"))){
                    player.openInventory(InventoryGenerator.createUpgradeTotalMembersInventory(player.getName()));
                }else if (player.getOpenInventory().getTitle().equals(StelyTeamPlugin.config.getString("inventoriesName.editMembers"))){
                    player.openInventory(InventoryGenerator.createEditMembersInventory(player.getName()));
                }else if (player.getOpenInventory().getTitle().equals(StelyTeamPlugin.config.getString("inventoriesName.teamMembers"))){
                    player.openInventory(InventoryGenerator.createMembersInventory(player.getName()));
                }else if (player.getOpenInventory().getTitle().equals(StelyTeamPlugin.config.getString("inventoriesName.permissions"))){
                    player.openInventory(InventoryGenerator.createPermissionsInventory(player.getName()));
                }
            }
        }
    }
}
