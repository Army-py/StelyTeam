package fr.army.stelyteam.utils.manager.database.builder.impl.holder;

import java.util.List;

import fr.army.stelyteam.cache.SaveField;

public interface FieldHolder {
    
    public void setFields(SaveField... fields);

    public SaveField[] getFields();

    public List<String> getFieldsName();
}
