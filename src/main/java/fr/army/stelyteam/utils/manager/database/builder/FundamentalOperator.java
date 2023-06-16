package fr.army.stelyteam.utils.manager.database.builder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fr.army.stelyteam.cache.SaveField;

public abstract class FundamentalOperator {
    
    protected String[] tables;
    protected SaveField[] columns;
    protected String[] conditions;


    public FundamentalOperator() {
    }


    @Nullable
    public abstract SaveField[] getColumns();

    public abstract String build();
}
