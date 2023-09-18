package fr.army.stelyteam.utils.loader.exception;

import org.jetbrains.annotations.NotNull;

public class UnableLoadConfigException extends Exception {
    
    public UnableLoadConfigException(@NotNull String fileName, @NotNull String reason) {
        super("Unable to load config file " + fileName + ": " + reason);
    }
}
