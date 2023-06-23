package fr.army.stelyteam.utils.manager.database.builder.impl;

import org.jetbrains.annotations.NotNull;

import fr.army.stelyteam.utils.manager.database.builder.impl.holder.FieldHolder;
import fr.army.stelyteam.utils.manager.database.builder.impl.holder.ValueHolder;

public interface IInsertQuery extends FieldHolder, ValueHolder, Query {
    
    public void setTable(@NotNull String table);

    @NotNull
    public String getTable();
}
