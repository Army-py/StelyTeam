package fr.army.stelyteam.utils;

import fr.army.stelyteam.StelyTeamPlugin;

public class MessageManager {
    private static String getPluginPrefix (){
        return StelyTeamPlugin.config.getString("prefix");
    }

    public static String getMessage(String path) {
        return getPluginPrefix() + StelyTeamPlugin.messages.getString(path);
        // return new ColorsBuilder().replaceColor(getPluginPrefix() + StelyTeamPlugin.messages.getString(path));
    }

    public static String getReplaceMessage(String path, String replace) {
        return getPluginPrefix() + StelyTeamPlugin.messages.getString(path).replace("%VALUE%", replace);
        // return new ColorsBuilder().replaceColor(getPluginPrefix() + StelyTeamPlugin.messages.getString(path).replace("%VALUE%", replace));
    }

    public static String getDoubleReplaceMessage(String path, String author, String receiver) {
        return getPluginPrefix() + StelyTeamPlugin.messages.getString(path).replace("%AUTHOR%", author).replace("%RECEIVER%", receiver);
        // return new ColorsBuilder().replaceColor(getPluginPrefix() + StelyTeamPlugin.messages.getString(path).replace("%AUTHOR%", author).replace("%RECEIVER%", receiver));
    }
}
