package fr.army.stelyteam.external;

import java.util.UUID;

public interface MessageHandler {

    void handleMessage(UUID senderId, boolean isPrefixed);

}