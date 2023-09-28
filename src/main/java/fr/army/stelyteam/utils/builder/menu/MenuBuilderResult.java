package fr.army.stelyteam.utils.builder.menu;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fr.army.stelyteam.menu.template.MenuTemplate;
import fr.army.stelyteam.menu.view.AbstractMenuView;

public class MenuBuilderResult<T extends AbstractMenuView<T>> {
    
    private final MenuTemplate<T> menuTemplate;
    private final YamlConfiguration config;

    public MenuBuilderResult(MenuTemplate<T> menuTemplate, YamlConfiguration config) {
        this.menuTemplate = menuTemplate;
        this.config = config;
    }

    @NotNull
    public MenuTemplate<T> getMenuTemplate() {
        return menuTemplate;
    }

    @Nullable
    public YamlConfiguration getConfig() {
        return config;
    }
}