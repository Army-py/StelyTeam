package fr.army.stelyteam.menu.button;

import fr.army.stelyteam.menu.button.impl.*;
import fr.army.stelyteam.menu.button.template.ButtonTemplate;
import fr.army.stelyteam.menu.view.AbstractMenuView;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import fr.army.stelyteam.StelyTeamPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public enum Buttons {

    /* CREATE / REMOVE TEAM */
    BUTTON_CREATE_TEAM(CreateTeamButton::new),
    BUTTON_REMOVE_TEAM(RemoveTeamButton::new),

    /* CONFIRM */
    BUTTON_CANCEL(CancelButton::new),
    BUTTON_CONFIRM_CREATE_TEAM(ConfirmCreateTeamButton::new),

    /* OPEN MENU */
    BUTTON_TEAM_MEMBERS_MENU(TeamMembersButton::new),
    BUTTON_TEAM_ALLIANCES_MENU(TeamAlliancesButton::new),
    BUTTON_TEAM_STORAGES_MENU(TeamStoragesButton::new),
    // BUTTON_OPEN_MEMBER_MENU("member"),
    // BUTTON_OPEN_EDIT_MEMBERS_MENU("editMembers"),
    // BUTTON_OPEN_EDIT_ALLIANCES_MENU("editAlliances"),
    // BUTTON_OPEN_EDIT_TEAM_PERMISSIONS_MENU("editPermissions"),
    // BUTTON_OPEN_TEAM_MEMBERS("seeTeamMembers"),
    // BUTTON_OPEN_TEAM_ALLIANCES("seeTeamAlliances"),// BUTTON_OPEN_STORAGE_DIRECTORY("storageDirectory"),

    /* BLANK BUTTON (no action) */
    BUTTON_TEAM_DETAILS(BlankButton::new),
    BUTTON_TEAM_BANK_DETAILS(BlankButton::new),
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
    BUTTON_BACK(BackButton::new),

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
    BUTTON_TEAM_BANK_ADD_MONEY(AddMoneyButton::new),

    BUTTON_TEAM_BANK_WITHDRAW_MONEY(WithdrawMoneyButton::new),

    /* ADD / REMOVE MEMBER */
    BUTTON_TEAM_LEAVE(TeamLeaveButton::new),
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

    BUTTON_BLANK(BlankButton::new),
            ;

    private final Function<ButtonTemplate, Button<?>> buttonSupplier;

    Buttons(Function<ButtonTemplate, Button<?>> buttonSupplier) {
        this.buttonSupplier = buttonSupplier;
    }

    @Nullable
    public Button<?> createButton(@NotNull ButtonTemplate buttonTemplate){
         try {
             return buttonSupplier.apply(buttonTemplate);
         } catch (Exception e) {
             return null;
         }
    }

    public boolean isEmptyCase(InventoryClickEvent clickEvent) {
        ItemStack itemStack = clickEvent.getCurrentItem();

        if (itemStack == null) return false;
        if (itemStack.getItemMeta() == null) return false;

        NamespacedKey key = new NamespacedKey(StelyTeamPlugin.getPlugin(), "emptyCase");
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (container.has(key, PersistentDataType.INTEGER)
                && container.get(key, PersistentDataType.INTEGER).equals(1)) return true;

        return false;
    }


    @NotNull
    public static Buttons getButtonType(@Nullable String name) throws IllegalArgumentException {
        if (name == null) return BUTTON_BLANK;
        return valueOf("BUTTON_" + name.toUpperCase());
    }
}
