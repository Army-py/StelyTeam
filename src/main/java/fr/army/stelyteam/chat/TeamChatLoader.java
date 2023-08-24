package fr.army.stelyteam.chat;

import org.bukkit.configuration.file.YamlConfiguration;

import fr.army.stelyteam.StelyTeamPlugin;

public class TeamChatLoader {

    private final YamlConfiguration config;

    public TeamChatLoader(StelyTeamPlugin plugin) {
        this.config = plugin.getConfig();
    }

    public TeamChatManager load() {
        final String format = config.getString("teamChat.format");
        final TeamChatProcessor processor = new TeamChatProcessor(format);
        return new TeamChatManager(processor);
    }
}
