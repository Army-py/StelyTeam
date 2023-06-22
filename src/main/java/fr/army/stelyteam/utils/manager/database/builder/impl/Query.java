package fr.army.stelyteam.utils.manager.database.builder.impl;

import org.jetbrains.annotations.NotNull;

public interface Query {

    @NotNull
    public String build();
}
