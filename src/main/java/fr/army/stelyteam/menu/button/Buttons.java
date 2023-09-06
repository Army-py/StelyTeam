package fr.army.stelyteam.menu.button;

import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import fr.army.stelyteam.StelyTeamPlugin;

public enum Buttons {
    
    /* CREATE / REMOVE TEAM */
    CREATE_TEAM_BUTTON("create"),
    REMOVE_TEAM_BUTTON("removeTeam"),

    /* OPEN MENU */
    MANAGE_MENU_BUTTON("manage"),
    TEAM_LIST_MENU_BUTTON("teamList"),
    MEMBER_MENU_BUTTON("member"),
    EDIT_MEMBERS_MENU_BUTTON("editMembers"),
    EDIT_ALLIANCES_MENU_BUTTON("editAlliances"),
    EDIT_TEAM_PERMISSIONS_MENU_BUTTON("editPermissions"),
    TEAM_MEMBERS_BUTTON("seeTeamMembers"),
    TEAM_ALLIANCES_BUTTON("seeTeamAlliances"),
    STORAGE_DIRECTORY_BUTTON("storageDirectory"),

    /* CLOSE MENU */
    CLOSE_ADMIN_MENU_BUTTON("close"),
    CLOSE_MANAGE_MENU_BUTTON("close"),
    CLOSE_MEMBER_MENU_BUTTON("close"),
    CLOSE_UPGRADE_LVL_MEMBERS_MENU_BUTTON("close"),
    CLOSE_UPGRADE_LVL_STORAGE_MENU_BUTTON("close"),
    CLOSE_TEAM_MEMBERS_MENU_BUTTON("close"),
    CLOSE_TEAM_ALLIANCES_MENU_BUTTON("close"),
    CLOSE_EDIT_MEMBERS_MENU_BUTTON("close"),
    CLOSE_EDIT_ALLIANCES_MENU_BUTTON("close"),
    CLOSE_PERMISSIONS_MENU_BUTTON("close"),
    CLOSE_STORAGE_MENU_BUTTON("close"),
    CLOSE_STORAGE_DIRECTORY_MENU_BUTTON("close"),
    CLOSE_TEAM_LIST_MENU_BUTTON("close"),

    /* CREATE / REMOVE HOME */
    SET_TEAM_HOME_BUTTON("setTeamHome"),
    REMOVE_TEAM_HOME_BUTTON("removeTeamHome"),

    /* BUY AN UPGRADE */
    BUY_TEAM_BANK_BUTTON("buyTeamBank"),
    BUY_TEAM_CLAIM_BUTTON("buyTeamClaim"),
    UPGRADE_LVL_MEMBERS_MENU_BUTTON("upgradeTotalMembers"),
    UPGRADE_LVL_STORAGE_MENU_BUTTON("upgradeStorageAmount"),
    
    /* EDIT ATTRIBUTES */
    EDIT_TEAM_NAME_BUTTON("editName"),
    EDIT_TEAM_PREFIX_BUTTON("editPrefix"),
    EDIT_TEAM_DESCRIPTION_BUTTON("editDescription"),
    
    /* BLANK BUTTON (no action) */
    TEAM_INFOS_BUTTON("teamInfos"),
    TEAM_BANK_BUTTON("seeTeamBank"),
    EMPTY_CASE("emptyCase"),

    /* ADD / WITHDRAW MONEY */
    ADD_MONEY_TEAM_BANK_BUTTON("addTeamMoney"),
    WITHDRAW_MONEY_TEAM_BANK_BUTTON("withdrawTeamMoney"),
    
    /* ADD / REMOVE MEMBER */
    LEAVE_TEAM_BUTTON("leaveTeam"),
    ADD_MEMBER_BUTTON("addMember"),
    REMOVE_MEMBER_BUTTON("removeMember"),

    /* CONFIRM / CANCEL */
    CONFIRM_BUTTON("confirm"),
    CANCEL_BUTTON("cancel"),

    /* PROMOTE / DEMOTE MEMBER */
    EDIT_OWNER_BUTTON("editOwner"),

    /* ADD / REMOVE ALLIANCE */
    ADD_ALLIANCE_BUTTON("addAlliance"),
    REMOVE_ALLIANCE_BUTTON("removeAlliance"),
    
    /* PERMISSIONS */
    PERMISSION_ADD_TEAM_MONEY_BUTTON("addTeamMoney"),
    PERMISSION_WITHDRAW_TEAM_MONEY_BUTTON("withdrawTeamMoney"),
    PERMISSION_SEE_TEAM_BANK_BUTTON("seeTeamBank"),
    PERMISSION_BUY_TEAM_BANK_BUTTON("buyTeamBank"),
    PERMISSION_ADD_MEMBER_BUTTON("addMember"),
    PERMISSION_REMOVE_MEMBER_BUTTON("removeMember"),
    PERMISSION_SEE_TEAM_MEMBERS_BUTTON("seeTeamMembers"),
    PERMISSION_UPGRADE_LVL_MEMBERS_BUTTON("upgradeTotalMembers"),
    PERMISSION_EDIT_MEMBERS_BUTTON("editMembers"),
    PERMISSION_EDIT_TEAM_NAME_BUTTON("editName"),
    PERMISSION_EDIT_TEAM_PREFIX_BUTTON("editPrefix"),
    PERMISSION_SET_TEAM_HOME_BUTTON("setTeamHome"),
    PERMISSION_REMOVE_TEAM_HOME_BUTTON("removeTeamHome"),
    PERMISSION_EDIT_PERMISSIONS_BUTTON("editPermissions"),
    PERMISSION_STORAGE_DIRECTORY_BUTTON("storageDirectory"),
    PERMISSION_UPGRADE_LVL_STORAGE_BUTTON("upgradeStorageAmount"),
    PERMISSION_ADD_ALLIANCE_BUTTON("addAlliance"),
    PERMISSION_REMOVE_ALLIANCE_BUTTON("removeAlliance"),
    PERMISSION_SEE_TEAM_ALLIANCES_BUTTON("seeTeamAlliances"),

    /* NEXT / PREVIOUS BUTTONS (must be reviewed) */
    PREVIOUS_STORAGE_BUTTON("previous"),
    NEXT_STORAGE_BUTTON("next"),
    PREVIOUS_TEAM_LIST_BUTTON("previous"),
    NEXT_TEAM_LIST_BUTTON("next"),
    ;

    private final String name;
    
    Buttons(String name){
        this.name = name;
    }

    public boolean isClickedButton(InventoryClickEvent event){
        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null) return false;
        if (itemStack.getItemMeta() == null) return false;

        ItemMeta meta = itemStack.getItemMeta();
        if (itemStack.getItemMeta() instanceof SkullMeta){
            meta = (SkullMeta) itemStack.getItemMeta();
        }

        if (meta.getDisplayName() == null) return false;

        NamespacedKey key = new NamespacedKey(StelyTeamPlugin.getPlugin(), "buttonName");
        meta = event.getCurrentItem().getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        String buttonName = container.get(key, PersistentDataType.STRING);

        if (buttonName == null) return false;
        if (buttonName.equals(this.name)) return true;

        return false;
    }

    public boolean isEmptyCase(InventoryClickEvent clickEvent){
        ItemStack itemStack = clickEvent.getCurrentItem();

        if(itemStack == null) return false;
        if(itemStack.getItemMeta() == null) return false;

        NamespacedKey key = new NamespacedKey(StelyTeamPlugin.getPlugin(), "emptyCase");
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if(container.has(key , PersistentDataType.INTEGER) 
            && container.get(key, PersistentDataType.INTEGER).equals(1)) return true;
        
        return false;
    }
}
