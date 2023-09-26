package fr.army.stelyteam.chat;

import fr.army.stelyteam.config.Config;

public class TeamChatLoader {

    public TeamChatLoader() {
    }

    public TeamChatManager load() {
        final String format = Config.teamChatFormat;
        final TeamChatProcessor processor = new TeamChatProcessor(format);
        return new TeamChatManager(processor);
    }
}
