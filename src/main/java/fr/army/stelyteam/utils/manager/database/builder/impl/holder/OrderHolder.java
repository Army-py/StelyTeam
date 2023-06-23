package fr.army.stelyteam.utils.manager.database.builder.impl.holder;

import org.jetbrains.annotations.Nullable;

public interface OrderHolder {
    
    public void setOrders(String... orders);

    @Nullable
    public String[] getOrders();
}
