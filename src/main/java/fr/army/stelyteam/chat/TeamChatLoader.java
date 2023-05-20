package fr.army.stelyteam.chat;

public class TeamChatLoader {

    public TeamChatManager load() {
        // TODO Init with the config
        final String format = "[Team] {USERNAME} > {MESSAGE}";
        final TeamChatProcessor processor = new TeamChatProcessor(format);
        return new TeamChatManager(processor);
    }
}
