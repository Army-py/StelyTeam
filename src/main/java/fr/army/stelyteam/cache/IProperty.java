package fr.army.stelyteam.cache;

import org.jetbrains.annotations.NotNull;

public interface IProperty {

    boolean isLoaded();

    @NotNull
    TeamField getField();

}
