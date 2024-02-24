package fr.army.stelyteam.utils.network;

public enum ChannelRegistry {

    STORAGE_CHANNEL("stelyteam:storage"),
    TEAM_CHAT_CHANNEL("stelyteam:team_chat"),
    ;

    private final String channel;

    ChannelRegistry(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }
}
