package fr.army.stelyteam.utils.manager.database.builder.impl.fundamental;


import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import fr.army.stelyteam.cache.SaveField;
import fr.army.stelyteam.utils.manager.database.builder.impl.ISelectQuery;

public class SelectQuery implements ISelectQuery {
    
    private final String headRequest = "SELECT {fields} FROM {table}";

    private String[] tables;
    private SaveField[] fields;
    private String[] orders;
    private String[] conditions;

    @Override
    public String build() {
        StringBuilder builder = new StringBuilder(headRequest);
        if (fields == null) {
            builder.replace(builder.indexOf("{fields}"), builder.indexOf("{fields}") + "{fields}".length(), "*");
        } else {
            builder.replace(builder.indexOf("{fields}"), builder.indexOf("{fields}") + "{fields}".length(), String.join(", ", getFieldsName()));
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

    @Override
    public List<String> getFieldsName() {
        List<String> list = new ArrayList<>();
        for(SaveField field : fields){
            list.add(field.getColumnName());
        }
        return list;
    }

    @Override
    public String[] getConditions() {
        return conditions;
    }

    @Override
    public String[] getTables() {
        return tables;
    }

    @Override
    public SaveField[] getFields() {
        return fields;
    }

    @Override
    public String[] getOrders() {
        return orders;
    }

    @Override
    public void setFields(SaveField... fields) {
        this.fields = fields;
    }

    @Override
    public void setConditions(String... conditions) {
        this.conditions = conditions;
    }

    @Override
    public void setTables(@NotNull String... tables) {
        this.tables = tables;
    }

    @Override
    public void setOrders(String... orders) {
        this.orders = orders;
    }
}
