package fr.army.stelyteam.utils.builder.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.menu.TeamMenu;
import fr.army.stelyteam.menu.button.Button;
import fr.army.stelyteam.menu.button.ButtonItem;
import fr.army.stelyteam.menu.button.impl.BlankButton;
import fr.army.stelyteam.menu.impl.EmptyMenu;

public class MenuBuilder {
    
    private static final MenuBuilder INSTANCE = new MenuBuilder();

    private final StelyTeamPlugin plugin = StelyTeamPlugin.getPlugin();

    public static MenuBuilder getInstance() {
        return INSTANCE;
    }


    public MenuBuilderResult loadMenu(String configName){
        return buildMenu(plugin.getConfigLoader().initFile("menus/" + configName));
    }


    private MenuBuilderResult buildMenu(YamlConfiguration config) {
        final String title = config.getString("title");
        final String[] pattern = config.getStringList("pattern").toArray(String[]::new);
        final List<Button> buttons = new ArrayList<>();

        int size = 0;
        for (int row = 0; row < pattern.length && row < 6; row++) {
            final String line = pattern[row].replace(" ", "");

            for (int column = 0; column < line.length() && column < 9; column++) {
                final char character = line.charAt(column);

                final String path = "items." + character + ".";

                final String material = config.getString(path + "material");
                final String name = config.getString(path + "name");
                final int amount = config.getInt(path + "amount");
                final List<String> lore = config.getStringList(path + "lore");
                final boolean glow = config.getBoolean(path + "is-glowing");
                final String skullTexture = config.getString(path + "skull-texture");

                final ButtonItem buttonItem = new ButtonItem(Material.valueOf(material), name, amount, lore, glow, skullTexture);
                final Button button = new BlankButton(character, buttonItem);

                buttons.add(button);
                size++;
            }
        }

        TeamMenu menu = new EmptyMenu(title, size);
        menu.addButtons(buttons.toArray(Button[]::new));

        return new MenuBuilderResult(menu, config);
    }
}

