package fr.army.stelyteam.utils.manager.database.builder.impl;

import org.jetbrains.annotations.NotNull;

import fr.army.stelyteam.utils.manager.database.builder.impl.holder.ConditionHolder;

public interface IDeleteQuery extends ConditionHolder, Query {
    
    public void setTable(@NotNull String table);

    public String getTable();
}
