package fr.army.stelyteam.utils.manager.database.builder.impl.query;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import fr.army.stelyteam.cache.Property;
import fr.army.stelyteam.cache.SaveField;
import fr.army.stelyteam.utils.manager.database.builder.impl.IInsertQuery;

public class InsertQuery implements IInsertQuery {

    private final String headRequest = "INSERT INTO {table} ({fields}) VALUES ({values})";

    private String table;
    private SaveField[] fields;
    private Property<?>[] values;

    @Override
    public String build() {
        StringBuilder builder = new StringBuilder(headRequest);
        builder.replace(builder.indexOf("{table}"), builder.indexOf("{table}") + "{table}".length(), table);
        builder.replace(builder.indexOf("{fields}"), builder.indexOf("{fields}") + "{fields}".length(),
                String.join(", ", getFieldsName()));
        builder.replace(builder.indexOf("{values}"), builder.indexOf("{values}") + "{values}".length(),
                String.join(", ", getValuesTag()));
        builder.append(";");
        return builder.toString();
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
    public void setFields(SaveField... fields) {
        this.fields = fields;
    }

    @Override
    public void setValues(@NotNull Property<?>... values) {
        this.values = values;
    }

    @Override
    public void setTable(@NotNull String table) {
        this.table = table;
    }
}
