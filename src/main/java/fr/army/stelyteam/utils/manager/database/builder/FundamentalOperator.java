package fr.army.stelyteam.utils.manager.database.builder;

import org.jetbrains.annotations.NotNull;

public abstract class FundamentalOperator {
    
    protected final String table;
    protected final String[] columns;
    protected final String[] conditions;


    public FundamentalOperator(@NotNull String table, @NotNull String[] columns, String[] conditions) {
        this.table = table;
        this.columns = columns;
        this.conditions = conditions;
    }


    // public abstract String getTable();

    // public abstract List<String> getColumns();

    // public abstract List<String> getConditions();

    public abstract String build();
}
