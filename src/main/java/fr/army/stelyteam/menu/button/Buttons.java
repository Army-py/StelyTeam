package fr.army.stelyteam.menu.button;

import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import fr.army.stelyteam.StelyTeamPlugin;

public enum Buttons {
    
    /* CREATE / REMOVE TEAM */
    BUTTON_CREATE_TEAM,
    BUTTON_REMOVE_TEAM,

    /* CONFIRM */
    BUTTON_CONFIRM,
    BUTTON_CANCEL,
    BUTTON_CONFIRM_CREATE_TEAM,

    /* OPEN MENU */
    BUTTON_TEAM_MEMBERS_MENU,
    BUTTON_TEAM_ALLIANCES_MENU,
    BUTTON_TEAM_STORAGES_MENU,
    // BUTTON_OPEN_MEMBER_MENU("member"),
    // BUTTON_OPEN_EDIT_MEMBERS_MENU("editMembers"),
    // BUTTON_OPEN_EDIT_ALLIANCES_MENU("editAlliances"),
    // BUTTON_OPEN_EDIT_TEAM_PERMISSIONS_MENU("editPermissions"),
    // BUTTON_OPEN_TEAM_MEMBERS("seeTeamMembers"),
    // BUTTON_OPEN_TEAM_ALLIANCES("seeTeamAlliances"),// BUTTON_OPEN_STORAGE_DIRECTORY("storageDirectory"),

    /* BLANK BUTTON (no action) */
    BUTTON_TEAM_DETAILS,
    BUTTON_TEAM_BANK_DETAILS,
    // BUTTON_TEAM_BANK("seeTeamBank"),
    // BUTTON_EMPTY_CASE("emptyCase"),

    /* BACK MENU */
    // BUTTON_CLOSE_ADMIN_MENU("close"),
    // BUTTON_CLOSE_MANAGE_MENU("close"),
    // BUTTON_CLOSE_MEMBER_MENU("close"),
    // BUTTON_CLOSE_UPGRADE_LVL_MEMBERS_MENU("close"),
    // BUTTON_CLOSE_UPGRADE_LVL_STORAGE_MENU("close"),
    // BUTTON_CLOSE_TEAM_MEMBERS_MENU("close"),
    // BUTTON_CLOSE_TEAM_ALLIANCES_MENU("close"),
    // BUTTON_CLOSE_EDIT_MEMBERS_MENU("close"),
    // BUTTON_CLOSE_EDIT_ALLIANCES_MENU("close"),
    // BUTTON_CLOSE_PERMISSIONS_MENU("close"),
    // BUTTON_CLOSE_STORAGE_MENU("close"),
    // BUTTON_CLOSE_STORAGE_DIRECTORY_MENU("close"),
    // BUTTON_CLOSE_TEAM_LIST_MENU("close"),
    BUTTON_BACK,

    /* CREATE / REMOVE HOME */
    // BUTTON_SET_TEAM_HOME("setTeamHome"),
    // BUTTON_REMOVE_TEAM_HOME("removeTeamHome"),

    /* BUY AN UPGRADE */
    // BUTTON_BUY_TEAM_BANK("buyTeamBank"),
    // BUTTON_BUY_TEAM_CLAIM("buyTeamClaim"),
    // BUTTON_UPGRADE_LVL_MEMBERS_MENU("upgradeTotalMembers"),
    // BUTTON_UPGRADE_LVL_STORAGE_MENU("upgradeStorageAmount"),
    
    /* EDIT ATTRIBUTES */
    // BUTTON_EDIT_TEAM_NAME("editName"),
    // BUTTON_EDIT_TEAM_PREFIX("editPrefix"),
    // BUTTON_EDIT_TEAM_DESCRIPTION("editDescription"),

    /* ADD / WITHDRAW MONEY */
     BUTTON_TEAM_BANK_ADD_MONEY,
     BUTTON_TEAM_BANK_WITHDRAW_MONEY,
    
    /* ADD / REMOVE MEMBER */
    // BUTTON_LEAVE_TEAM("leaveTeam"),
    // BUTTON_ADD_MEMBER("addMember"),
    // BUTTON_REMOVE_MEMBER("removeMember"),

    /* PROMOTE / DEMOTE MEMBER */
    // BUTTON_EDIT_OWNER("editOwner"),

    /* ADD / REMOVE ALLIANCE */
    // BUTTON_ADD_ALLIANCE("addAlliance"),
    // BUTTON_REMOVE_ALLIANCE("removeAlliance"),
    
    /* PERMISSIONS */
    // BUTTON_PERMISSION_ADD_TEAM_MONEY("addTeamMoney"),
    // BUTTON_PERMISSION_WITHDRAW_TEAM_MONEY("withdrawTeamMoney"),
    // BUTTON_PERMISSION_SEE_TEAM_BANK("seeTeamBank"),
    // BUTTON_PERMISSION_BUY_TEAM_BANK("buyTeamBank"),
    // BUTTON_PERMISSION_ADD_MEMBER("addMember"),
    // BUTTON_PERMISSION_REMOVE_MEMBER("removeMember"),
    // BUTTON_PERMISSION_SEE_TEAM_MEMBERS("seeTeamMembers"),
    // BUTTON_PERMISSION_UPGRADE_LVL_MEMBERS("upgradeTotalMembers"),
    // BUTTON_PERMISSION_EDIT_MEMBERS("editMembers"),
    // BUTTON_PERMISSION_EDIT_TEAM_NAME("editName"),
    // BUTTON_PERMISSION_EDIT_TEAM_PREFIX("editPrefix"),
    // BUTTON_PERMISSION_SET_TEAM_HOME("setTeamHome"),
    // BUTTON_PERMISSION_REMOVE_TEAM_HOME("removeTeamHome"),
    // BUTTON_PERMISSION_EDIT_PERMISSIONS("editPermissions"),
    // BUTTON_PERMISSION_STORAGE_DIRECTORY("storageDirectory"),
    // BUTTON_PERMISSION_UPGRADE_LVL_STORAGE("upgradeStorageAmount"),
    // BUTTON_PERMISSION_ADD_ALLIANCE("addAlliance"),
    // BUTTON_PERMISSION_REMOVE_ALLIANCE("removeAlliance"),
    // BUTTON_PERMISSION_SEE_TEAM_ALLIANCES("seeTeamAlliances"),

    /* NEXT / PREVIOUS BUTTONS (must be reviewed) */
    // BUTTON_PREVIOUS_STORAGE("previous"),
    // BUTTON_NEXT_STORAGE("next"),
    // BUTTON_PREVIOUS_TEAM_LIST("previous"),
    // BUTTON_NEXT_TEAM_LIST("next"),
    ;

    // private final String name;
    
    // Buttons(String name){
    //     this.name = name;
    // }

    // public boolean isClickedButton(InventoryClickEvent event){
    //     ItemStack itemStack = event.getCurrentItem();
    //     if (itemStack == null) return false;
    //     if (itemStack.getItemMeta() == null) return false;

    //     ItemMeta meta = itemStack.getItemMeta();
    //     if (itemStack.getItemMeta() instanceof SkullMeta){
    //         meta = (SkullMeta) itemStack.getItemMeta();
    //     }

    //     if (meta.getDisplayName() == null) return false;

    //     NamespacedKey key = new NamespacedKey(StelyTeamPlugin.getPlugin(), "buttonName");
    //     meta = event.getCurrentItem().getItemMeta();
    //     PersistentDataContainer container = meta.getPersistentDataContainer();
    //     String buttonName = container.get(key, PersistentDataType.STRING);

    //     if (buttonName == null) return false;
    //     if (buttonName.equals(this.name)) return true;

    //     return false;
    // }

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


    public static Buttons getButtonType(String name){
        return valueOf("BUTTON_" + name.toUpperCase());
    }
}
