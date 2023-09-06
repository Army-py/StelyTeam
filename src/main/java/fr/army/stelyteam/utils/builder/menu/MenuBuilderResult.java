package fr.army.stelyteam.utils.builder.menu;

import org.bukkit.configuration.file.YamlConfiguration;

import fr.army.stelyteam.menu.TeamMenuOLD;

public class MenuBuilderResult {
    
    private final TeamMenuOLD menu;
    private final YamlConfiguration config;

    public MenuBuilderResult(TeamMenuOLD menu, YamlConfiguration config) {
        this.menu = menu;
        this.config = config;
    }

    public TeamMenuOLD getMenu() {
        return menu;
    }

    public YamlConfiguration getConfig() {
        return config;
    }
}