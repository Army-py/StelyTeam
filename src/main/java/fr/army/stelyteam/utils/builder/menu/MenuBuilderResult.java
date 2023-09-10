package fr.army.stelyteam.utils.builder.menu;

import org.bukkit.configuration.file.YamlConfiguration;

import fr.army.stelyteam.menu.template.MenuTemplate;

public class MenuBuilderResult {
    
    private final MenuTemplate menuTemplate;
    private final YamlConfiguration config;

    public MenuBuilderResult(MenuTemplate menuTemplate, YamlConfiguration config) {
        this.menuTemplate = menuTemplate;
        this.config = config;
    }

    public MenuTemplate getMenuTemplate() {
        return menuTemplate;
    }

    public YamlConfiguration getConfig() {
        return config;
    }
}