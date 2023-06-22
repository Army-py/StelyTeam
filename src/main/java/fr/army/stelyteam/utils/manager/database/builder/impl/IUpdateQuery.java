package fr.army.stelyteam.utils.manager.database.builder.impl;

import fr.army.stelyteam.utils.manager.database.builder.impl.holder.ConditionHolder;
import fr.army.stelyteam.utils.manager.database.builder.impl.holder.FieldHolder;
import fr.army.stelyteam.utils.manager.database.builder.impl.holder.ValueHolder;

public interface IUpdateQuery extends FieldHolder, ValueHolder, ConditionHolder, Query {
    
    public void setTable(String table);

    public String getTable();
}
