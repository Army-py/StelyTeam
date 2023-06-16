package fr.army.stelyteam.utils.manager.database.builder.fundamental;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import fr.army.stelyteam.cache.SaveField;
import fr.army.stelyteam.utils.manager.database.builder.FundamentalOperator;

public class SelectOperator extends FundamentalOperator {
    
    private final String headRequest = "SELECT {columns} FROM {table}";

    private final String[] orders;
    
    public SelectOperator(@Nullable String... orders) {
        super();
        this.orders = orders;
    }

    @Override
    public String build() {
        StringBuilder builder = new StringBuilder(headRequest);
        if (columns == null) {
            builder.replace(builder.indexOf("{columns}"), builder.indexOf("{columns}") + "{columns}".length(), "*");
        } else {
            builder.replace(builder.indexOf("{columns}"), builder.indexOf("{columns}") + "{columns}".length(), String.join(", ", getColumnsName()));
        }
        builder.replace(builder.indexOf("{table}"), builder.indexOf("{table}") + "{table}".length(), String.join(", ", tables));
        if (conditions != null) {
            builder.append(" WHERE ");
            builder.append(String.join(" AND ", conditions));
        }
        if (orders != null){
            builder.append(" ORDER BY ");
            builder.append(String.join(", ", orders));
        }
        builder.append(";");
        return builder.toString();
    }


    @Nullable
    @Override
    public SaveField[] getColumns() {
        return columns;
    }


    private List<String> getColumnsName(){
        List<String> list = new ArrayList<>();
        for(SaveField field : columns){
            list.add(field.getColumnName());
        }
        return list;
    }

    public void setTables(@NotNull String... tables) {
        this.tables = tables;
    }

    public void setColumns(@NotNull SaveField... columns) {
        this.columns = columns;
    }

    public void setConditions(@NotNull String... conditions) {
        this.conditions = conditions;
    }

}
