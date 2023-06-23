package fr.army.stelyteam.utils.manager.database.builder.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fr.army.stelyteam.utils.manager.database.builder.impl.holder.ConditionHolder;
import fr.army.stelyteam.utils.manager.database.builder.impl.holder.FieldHolder;
import fr.army.stelyteam.utils.manager.database.builder.impl.holder.OrderHolder;

public interface ISelectQuery extends FieldHolder, ConditionHolder, OrderHolder, Query {
    
    public void setTables(@NotNull String... tables);

    @Nullable
    public String[] getTables();
}
