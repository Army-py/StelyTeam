package fr.army.stelyteam.menu;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import fr.army.stelyteam.StelyTeamPlugin;

public enum Buttons {

    CREATE_TEAM_BUTTON("create", "createTeam"),
    MANAGE_MENU_BUTTON("manage", "admin"),
    TEAM_LIST_MENU_BUTTON("teamList", "admin"), 
    MEMBER_MENU_BUTTON("member", "admin"),
    CLOSE_ADMIN_MENU_BUTTON("close", "admin"),
    EDIT_MEMBERS_MENU_BUTTON("editMembers", "manage"),
    EDIT_ALLIANCES_MENU_BUTTON("editAlliances", "manage"),
    SET_TEAM_HOME_BUTTON("setTeamHome", "manage"),
    REMOVE_TEAM_HOME_BUTTON("removeTeamHome", "manage"),
    BUY_TEAM_BANK_BUTTON("buyTeamBank", "manage"),
    UPGRADE_LVL_MEMBERS_MENU_BUTTON("upgradeTotalMembers", "manage"),
    UPGRADE_LVL_STORAGE_MENU_BUTTON("upgradeStorageAmount", "manage"),
    EDIT_TEAM_NAME_BUTTON("editName", "manage"),
    EDIT_TEAM_PREFIX_BUTTON("editPrefix", "manage"),
    EDIT_TEAM_DESCRIPTION_BUTTON("editDescription", "manage"),
    EDIT_TEAM_PERMISSIONS_MENU_BUTTON("editPermissions", "manage"),
    REMOVE_TEAM_BUTTON("removeTeam", "manage"),
    CLOSE_MANAGE_MENU_BUTTON("close", "manage"),
    TEAM_INFOS_BUTTON("teamInfos", "member"),
    TEAM_MEMBERS_BUTTON("seeTeamMembers", "member"),
    TEAM_ALLIANCES_BUTTON("seeTeamAlliances", "member"),
    TEAM_BANK_BUTTON("seeTeamBank", "member"),
    ADD_MONEY_TEAM_BANK_BUTTON("addTeamMoney", "member"),
    WITHDRAW_MONEY_TEAM_BANK_BUTTON("withdrawTeamMoney", "member"),
    STORAGE_DIRECTORY_BUTTON("storageDirectory", "member"),
    LEAVE_TEAM_BUTTON("leaveTeam", "member"),
    CLOSE_MEMBER_MENU_BUTTON("close", "member"),
    CLOSE_UPGRADE_LVL_MEMBERS_MENU_BUTTON("close", "upgradeTotalMembers"),
    CLOSE_UPGRADE_LVL_STORAGE_MENU_BUTTON("close", "upgradeStorageAmount"),
    CONFIRM_BUTTON("confirm", "confirmInventory"),
    CANCEL_BUTTON("cancel", "confirmInventory"),
    CLOSE_TEAM_MEMBERS_MENU_BUTTON("close", "teamMembers"),
    CLOSE_TEAM_ALLIANCES_MENU_BUTTON("close", "teamAlliances"),
    ADD_MEMBER_BUTTON("addMember", "editMembers"),
    REMOVE_MEMBER_BUTTON("removeMember", "editMembers"),
    EDIT_OWNER_BUTTON("editOwner", "editMembers"),
    CLOSE_EDIT_MEMBERS_MENU_BUTTON("close", "editMembers"),
    ADD_ALLIANCE_BUTTON("addAlliance", "editAlliances"),
    REMOVE_ALLIANCE_BUTTON("removeAlliance", "editAlliances"),
    CLOSE_EDIT_ALLIANCES_MENU_BUTTON("close", "editAlliances"),
    PERMISSION_ADD_TEAM_MONEY_BUTTON("addTeamMoney", "permissions"),
    PERMISSION_WITHDRAW_TEAM_MONEY_BUTTON("withdrawTeamMoney", "permissions"),
    PERMISSION_SEE_TEAM_BANK_BUTTON("seeTeamBank", "permissions"),
    PERMISSION_BUY_TEAM_BANK_BUTTON("buyTeamBank", "permissions"),
    PERMISSION_ADD_MEMBER_BUTTON("addMember", "permissions"),
    PERMISSION_REMOVE_MEMBER_BUTTON("removeMember", "permissions"),
    PERMISSION_SEE_TEAM_MEMBERS_BUTTON("seeTeamMembers", "permissions"),
    PERMISSION_UPGRADE_LVL_MEMBERS_BUTTON("upgradeTotalMembers", "permissions"),
    PERMISSION_EDIT_MEMBERS_BUTTON("editMembers", "permissions"),
    PERMISSION_EDIT_TEAM_NAME_BUTTON("editName", "permissions"),
    PERMISSION_EDIT_TEAM_PREFIX_BUTTON("editPrefix", "permissions"),
    PERMISSION_SET_TEAM_HOME_BUTTON("setTeamHome", "permissions"),
    PERMISSION_REMOVE_TEAM_HOME_BUTTON("removeTeamHome", "permissions"),
    PERMISSION_EDIT_PERMISSIONS_BUTTON("editPermissions", "permissions"),
    PERMISSION_STORAGE_DIRECTORY_BUTTON("storageDirectory", "permissions"),
    PERMISSION_UPGRADE_LVL_STORAGE_BUTTON("upgradeStorageAmount", "permissions"),
    PERMISSION_ADD_ALLIANCE_BUTTON("addAlliance", "permissions"),
    PERMISSION_REMOVE_ALLIANCE_BUTTON("removeAlliance", "permissions"),
    PERMISSION_SEE_TEAM_ALLIANCES_BUTTON("seeTeamAlliances", "permissions"),
    CLOSE_PERMISSIONS_MENU_BUTTON("close", "permissions"),
    PREVIOUS_STORAGE_BUTTON("previous", "storage"),
    NEXT_STORAGE_BUTTON("next", "storage"),
    CLOSE_STORAGE_MENU_BUTTON("close", "storage"),
    CLOSE_STORAGE_DIRECTORY_MENU_BUTTON("close", "storageDirectory"),
    NEXT_TEAM_LIST_BUTTON("next", "teamList"),
    PREVIOUS_TEAM_LIST_BUTTON("previous", "teamList"),
    CLOSE_TEAM_LIST_MENU_BUTTON("close", "teamList"),
    EMPTY_CASE("emptyCase");


    private final YamlConfiguration config = StelyTeamPlugin.getPlugin().getConfig();

    private final String name;
    private final String menuName;
    
    Buttons(String name, String menuName){
        this.name = name;
        this.menuName = menuName;
    }

    Buttons(String path){
        this.name = path;
        this.menuName = null;
    }

    public String getInventoryConfigPath(){
        return "inventories." + this.menuName + "." + this.name;
    }

    public String getInventoryButtonName(){
        return config.getString(getInventoryConfigPath() + ".itemName");
    }

    public Material getInventoryButtonMaterial(){
        return Material.getMaterial(config.getString(getInventoryConfigPath() + ".itemType"));
    }

    public int getInventoryButtonSlot(){
        return config.getInt(getInventoryConfigPath() + ".slot");
    }

    public List<Integer> getInventoryButtonSlots(){
        return config.getIntegerList(getInventoryConfigPath() + ".slots");
    }

    public List<String> getInventoryButtonLore(){
        return config.getStringList(getInventoryConfigPath() + ".lore");
    }

    public boolean isClickedButton(InventoryClickEvent event){
        ItemStack itemStack = event.getCurrentItem();
        // System.out.println(1);
        if(itemStack == null) return false;
        // System.out.println(2);
        if(itemStack.getItemMeta() == null) return false;
        // System.out.println(3);
        if(itemStack.getItemMeta().getDisplayName() == null) return false;
        // System.out.println(4);
        
        // System.out.println(itemStack.getItemMeta().getDisplayName().equals(getInventoryButtonName()));
        // System.out.println(itemStack.getType() == getInventoryButtonMaterial());
        // // System.out.println(!getInventoryButtonLore().isEmpty() || itemStack.getItemMeta().getLore().equals(getInventoryButtonLore()));
        // System.out.println(!getInventoryButtonLore().isEmpty());
        // System.out.println(itemStack.getItemMeta().getLore() != null);
        // // System.out.println(itemStack.getItemMeta().getLore().equals(getInventoryButtonLore()));
        // System.out.println((!getInventoryButtonLore().isEmpty() == (itemStack.getItemMeta().getLore() != null)
        //     || itemStack.getItemMeta().getLore().equals(getInventoryButtonLore())));
        // System.out.println((getInventoryButtonSlot() != 0 || event.getSlot() == getInventoryButtonSlot()));
        // System.out.println((!getInventoryButtonSlots().isEmpty() || getInventoryButtonSlots().contains(event.getSlot())));
        // System.out.println(((getInventoryButtonSlot() != 0 && event.getSlot() == getInventoryButtonSlot())
        // || (getInventoryButtonSlots().size() != 0 && getInventoryButtonSlots().contains(event.getSlot()))));
        
        if (itemStack.getItemMeta().getDisplayName().equals(getInventoryButtonName())
            && itemStack.getType() == getInventoryButtonMaterial()
            && (!getInventoryButtonLore().isEmpty() == (itemStack.getItemMeta().getLore() != null)
                || itemStack.getItemMeta().getLore().equals(getInventoryButtonLore()))
            && ((getInventoryButtonSlot() != 0 && event.getSlot() == getInventoryButtonSlot())
                || (getInventoryButtonSlots().size() != 0 && getInventoryButtonSlots().contains(event.getSlot())))) {
            return true;
        }
        // System.out.println(5);

        return false;
    }

    public boolean isEmptyCase(InventoryClickEvent clickEvent){
        ItemStack itemStack = clickEvent.getCurrentItem();

        // System.out.println(1);
        if(itemStack == null) return false;
        // System.out.println(2);
        if(itemStack.getItemMeta() == null) return false;
        // System.out.println(3);

        NamespacedKey key = new NamespacedKey(StelyTeamPlugin.getPlugin(), "emptyCase");
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if(container.has(key , PersistentDataType.INTEGER) 
            && container.get(key, PersistentDataType.INTEGER).equals(1)) return true;
        
        // System.out.println(4);
        return false;
    }
}
