package fr.army.stelyteam.utils;

import org.bukkit.configuration.file.YamlConfiguration;

import fr.army.stelyteam.StelyTeamPlugin;

public class MessageManager {

    private YamlConfiguration config;
    private YamlConfiguration messages;

    public MessageManager(StelyTeamPlugin plugin) {
        this.config = plugin.getConfig();
        this.messages = plugin.getMessages();
    }


    public String getMessage(String path) {
        return getPluginPrefix() + messages.getString(path);
        // return new ColorsBuilder().replaceColor(getPluginPrefix() + StelyTeamPlugin.messages.getString(path));
    }

    public String getReplaceMessage(String path, String replace) {
        return getPluginPrefix() + messages.getString(path).replace("%VALUE%", replace);
        // return new ColorsBuilder().replaceColor(getPluginPrefix() + StelyTeamPlugin.messages.getString(path).replace("%VALUE%", replace));
    }

    public String getDoubleReplaceMessage(String path, String author, String receiver) {
        return getPluginPrefix() + messages.getString(path).replace("%AUTHOR%", author).replace("%RECEIVER%", receiver);
        // return new ColorsBuilder().replaceColor(getPluginPrefix() + StelyTeamPlugin.messages.getString(path).replace("%AUTHOR%", author).replace("%RECEIVER%", receiver));
    }

    private String getPluginPrefix (){
        return config.getString("prefix");
    }
}
