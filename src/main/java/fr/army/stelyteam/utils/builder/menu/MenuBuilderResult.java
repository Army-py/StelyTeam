package fr.army.stelyteam.utils.builder.menu;

import org.bukkit.configuration.file.YamlConfiguration;

import fr.army.stelyteam.menu.TeamMenu;

public class MenuBuilderResult {
    
    private final TeamMenu menu;
    private final YamlConfiguration config;

    public MenuBuilderResult(TeamMenu menu, YamlConfiguration config) {
        this.menu = menu;
        this.config = config;
    }

    public TeamMenu getMenu() {
        return menu;
    }

    public YamlConfiguration getConfig() {
        return config;
    }
}