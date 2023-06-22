package fr.army.stelyteam.utils.manager.database.builder.impl.holder;

public interface ConditionHolder {
    
    public void setConditions(String... conditions);

    public String[] getConditions();
}
