package fr.army.stelyteam.utils.manager.database.builder.impl.holder;

import org.jetbrains.annotations.Nullable;

public interface ConditionHolder {
    
    public void setConditions(@Nullable String... conditions);

    @Nullable
    public String[] getConditions();
}
