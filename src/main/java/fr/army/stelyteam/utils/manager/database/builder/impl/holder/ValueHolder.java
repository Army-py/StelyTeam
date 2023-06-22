package fr.army.stelyteam.utils.manager.database.builder.impl.holder;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import fr.army.stelyteam.cache.Property;

public interface ValueHolder {
    
    public void setValues(@NotNull Property<?>... values);

    public List<Object> getValues();

    public List<String> getValuesTag();
}
