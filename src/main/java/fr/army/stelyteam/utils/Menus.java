package fr.army.stelyteam.utils;

import fr.army.stelyteam.StelyTeamPlugin;

public enum Menus {
    
    CREATE_TEAM_MENU("createTeam"),
    TEAM_LIST_MENU("teamList"),
    ADMIN_MENU("admin"),
    MANAGE_MENU("manage"),
    MEMBER_MENU("member"),
    TEAM_MEMBERS_MENU("teamMembers"),
    REMOVE_MEMBERS_MENU("removeMembers"),
    TEAM_ALLIANCES_MENU("teamAlliances"),
    REMOVE_ALLIANCES_MENU("removeAlliances"),
    EDIT_MEMBERS_MENU("editMembers"),
    EDIT_ALLIANCES_MENU("editAlliances"),
    UPGRADE_LVL_MEMBERS_MENU("upgradeTotalMembers"),
    CONFIRM_MENU("confirmInventory"),
    PERMISSIONS_MENU("permissions"),
    UPGRADE_LVL_STORAGE_MENU("upgradeStorageAmount"),
    STORAGE_DIRECTORY_MENU("storageDirectory"),
    STORAGE_MENU("storage"),
    STORAGE_ONE_MENU("storages.storageOne"),
    STORAGE_TWO_MENU("storages.storageTwo"),
    STORAGE_THREE_MENU("storages.storageThree"),
    STORAGE_FOUR_MENU("storages.storageFour"),
    STORAGE_FIVE_MENU("storages.storageFive");


    private final StelyTeamPlugin plugin = StelyTeamPlugin.getPlugin();
    private final String configPath;


    Menus(String configPath){
        this.configPath = configPath;
    }


    public String getName(){
        return this.plugin.getConfig().getString("inventoriesName." + this.configPath);
    }

    public int getSlots(){
        return this.plugin.getConfig().getInt("inventoriesSlots." + this.configPath);
    }

    public static String getStorageMenuName(int storageId){
        return StelyTeamPlugin.getPlugin().getConfig().getString(
            StelyTeamPlugin.getPlugin().getConfig().getString(
                "inventories.storageDirectory."+StelyTeamPlugin.getPlugin().getStorageFromId(storageId)+".itemName"
            )
        );
    }
}
