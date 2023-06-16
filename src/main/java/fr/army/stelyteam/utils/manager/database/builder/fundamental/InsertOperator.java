package fr.army.stelyteam.utils.manager.database.builder.fundamental;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fr.army.stelyteam.cache.Property;
import fr.army.stelyteam.cache.SaveField;
import fr.army.stelyteam.utils.manager.database.builder.FundamentalOperator;

public class InsertOperator extends FundamentalOperator {

    private final String headRequest = "INSERT INTO {table} ({columns}) VALUES ({values})";

    private Property<?>[] values;

    public InsertOperator() {
        super();
    }


    public void setTables(@NotNull String... tables) {
        this.tables = tables;
    }

    public void setColumns(@NotNull SaveField... columns) {
        this.columns = columns;
    }

    public void setValues(@NotNull Property<?>... values) {
        this.values = values;
    }

    @Override
    public @Nullable SaveField[] getColumns() {
        return columns;
    }

    @Override
    public String build() {
        StringBuilder builder = new StringBuilder(headRequest);
        builder.replace(builder.indexOf("{table}"), builder.indexOf("{table}") + "{table}".length(), String.join(", ", tables));
        builder.replace(builder.indexOf("{columns}"), builder.indexOf("{columns}") + "{columns}".length(), String.join(", ", getColumnsName()));
        builder.replace(builder.indexOf("{values}"), builder.indexOf("{values}") + "{values}".length(), String.join(", ", getValues()));
        builder.append(";");
        return builder.toString();
    }
    
    private List<String> getColumnsName(){
        List<String> list = new ArrayList<>();
        for(SaveField field : columns){
            list.add(field.getColumnName());
        }
        return list;
    }

    private List<String> getValues(){
        
    }
}
