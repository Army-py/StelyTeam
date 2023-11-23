package fr.army.stelyteam.utils.manager.database.builder.impl.query;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fr.army.stelyteam.utils.manager.database.builder.impl.IDeleteQuery;

public class DeleteQuery implements IDeleteQuery {
    
    private final String headRequest = "DELETE FROM {table}";

    private String table;
    private String[] conditions;

    @Override
    public @NotNull String build() {
        StringBuilder builder = new StringBuilder(headRequest);
        builder.replace(builder.indexOf("{table}"), builder.indexOf("{table}") + "{table}".length(), table);
        if (conditions != null) {
            builder.append(" WHERE ");
            builder.append(String.join(" AND ", conditions));
        }
        builder.append(";");
        return builder.toString();
    }

    @Override
    public @NotNull String getTable() {
        return table;
    }

    @Override
    public @Nullable String[] getConditions() {
        return conditions;
    }

    @Override
    public void setTable(@NotNull String table) {
        this.table = table;
    }

    @Override
    public void setConditions(@Nullable String... conditions) {
        this.conditions = conditions;
    }


}
