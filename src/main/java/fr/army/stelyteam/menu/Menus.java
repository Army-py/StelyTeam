package fr.army.stelyteam.menu;

import fr.army.stelyteam.menu.impl.AlliancesMenu;
import fr.army.stelyteam.menu.impl.ConfirmCreateTeamMenu;
import fr.army.stelyteam.menu.impl.CreateTeamMenu;
import fr.army.stelyteam.menu.view.impl.MenuView;
import fr.army.stelyteam.menu.view.impl.PagedMenuView;
import fr.army.stelyteam.team.Alliance;

public class Menus {
    
    public static TeamMenu<MenuView> MENU_CREATE_TEAM;
    public static TeamMenu<MenuView> MENU_CONFIRM_CREATE_TEAM;
    public static TeamMenu MENU_TEAMS;
    public static TeamMenu MENU_TEAM_MANAGE;
    public static TeamMenu MENU_HOME; // previously MEMBER_MENU
    public static TeamMenu MENU_TEAM_MEMBERS;
    public static TeamMenu<PagedMenuView<Alliance>> MENU_TEAM_ALLIANCES;
    public static TeamMenu MENU_MEMBERS_MANAGE;
    public static TeamMenu MENU_ALLIANCES_MANAGE;
    public static TeamMenu MENU_IMPROVE_MEMBER_LENGTH;
    public static TeamMenu MENU_IMPROVE_STORAGE_LENGTH;
    public static TeamMenu MENU_TEAM_PERMISSIONS;
    public static TeamMenu MENU_STORAGE_DIRECTORY;
    public static TeamMenu MENU_STORAGE;
    

    public void init() {
        MENU_CREATE_TEAM = CreateTeamMenu.createInstance("create_team");
        MENU_CONFIRM_CREATE_TEAM = ConfirmCreateTeamMenu.createInstance("confirm_create_team");
        MENU_TEAM_ALLIANCES = AlliancesMenu.createInstance("team_alliances");
    }
}
