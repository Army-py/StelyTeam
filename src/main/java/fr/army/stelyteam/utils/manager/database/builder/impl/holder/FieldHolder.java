package fr.army.stelyteam.utils.manager.database.builder.impl.holder;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import fr.army.stelyteam.cache.SaveField;

public interface FieldHolder {
    
    public void setFields(@NotNull SaveField... fields);

    @NotNull
    public SaveField[] getFields();

    @NotNull
    public List<String> getFieldsName();
}
