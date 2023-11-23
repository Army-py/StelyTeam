package fr.army.stelyteam.utils.manager.database.builder.impl.query;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import fr.army.stelyteam.cache.Property;
import fr.army.stelyteam.cache.SaveField;
import fr.army.stelyteam.utils.manager.database.builder.impl.IUpdateQuery;

public class UpdateQuery implements IUpdateQuery {

    private final String headRequest = "UPDATE {table} SET {fields}";

    private String table;
    private SaveField[] fields;
    private Property<?>[] values;
    private String[] conditions;


    @Override
    public @NotNull String build() {
        StringBuilder builder = new StringBuilder(headRequest);
        builder.replace(builder.indexOf("{table}"), builder.indexOf("{table}") + "{table}".length(), table);
        builder.replace(builder.indexOf("{fields}"), builder.indexOf("{fields}") + "{fields}".length(),
                String.join(", ", getFieldsName()));
        if (conditions != null) {
            builder.append(" WHERE ");
            builder.append(String.join(" AND ", conditions));
        }
        builder.append(";");
        return builder.toString();
    }


    @Override
    public String getTable() {
        return table;
    }

    @Override
    public SaveField[] getFields() {
        return fields;
    }

    @Override
    public List<Object> getValues() {
        List<Object> list = new ArrayList<>();
        for (Property<?> property : values) {
            list.add(property.get());
        }
        return list;
    }

    @Override
    public String[] getConditions() {
        return conditions;
    }

    @Override
    public List<String> getFieldsName() {
        List<String> list = new ArrayList<>();
        for (SaveField field : fields) {
            list.add(field.getColumnName());
        }
        return list;
    }

    @Override
    public List<String> getValuesTag() {
        List<String> list = new ArrayList<>();
        for (Property<?> property : values) {
            list.add("?");
        }
        return list;
    }

    @Override
    public void setTable(@NotNull String table) {
        this.table = table;
    }

    @Override
    public void setFields(SaveField... fields) {
        this.fields = fields;
    }

    @Override
    public void setValues(@NotNull Property<?>... values) {
        this.values = values;
    }

    @Override
    public void setConditions(String... conditions) {
        this.conditions = conditions;
    }
}
