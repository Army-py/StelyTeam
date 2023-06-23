package fr.army.stelyteam.utils.manager.database.builder.impl.holder;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import fr.army.stelyteam.cache.Property;

public interface ValueHolder {
    
    public void setValues(@NotNull Property<?>... values);

    @NotNull
    public List<Object> getValues();

    @NotNull
    public List<String> getValuesTag();
}
