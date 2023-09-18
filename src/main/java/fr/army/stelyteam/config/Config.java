package fr.army.stelyteam.config;

import org.bukkit.configuration.file.YamlConfiguration;

public class Config {

    private final YamlConfiguration config;
    
    public static String language;
    public static String[] serverNames;
    public static boolean enableTeamChat;
    public static String teamChatFormat;


    public Config(YamlConfiguration config) {
        this.config = config;
    }


    public void load(){
        language = this.config.getString("language");
        serverNames = this.config.getStringList("server-names").toArray(new String[0]);
        // TODO: voir pour changer new String[0] par String[]::new
        enableTeamChat = this.config.getBoolean("team-chat.enable");
        teamChatFormat = this.config.getString("team-chat.format");
    }
}
