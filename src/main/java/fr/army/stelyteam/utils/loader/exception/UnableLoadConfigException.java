package fr.army.stelyteam.utils.loader.exception;

import org.jetbrains.annotations.NotNull;

import fr.army.stelyteam.StelyTeamPlugin;

public class UnableLoadConfigException extends Exception {
    
    public UnableLoadConfigException(@NotNull String fileName, @NotNull String reason) {
        super("Unable to load config file " + fileName + ": " + reason);
        StelyTeamPlugin.getPlugin().getLogger().severe("Unable to load config file " + fileName + ": " + reason);
    }
}
