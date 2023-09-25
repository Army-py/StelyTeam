package fr.army.stelyteam.config.message.exception;

import org.jetbrains.annotations.NotNull;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.config.message.Messages;

public class MessageNotFoundException extends Exception {

    public MessageNotFoundException(@NotNull Messages message, @NotNull String fileName){
        super(message.toString() + " not found in " + fileName);
        StelyTeamPlugin.getPlugin().getLogger().severe(message.toString() + " not found in " + fileName);
    }
}
