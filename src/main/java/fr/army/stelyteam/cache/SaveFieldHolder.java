package fr.army.stelyteam.cache;

public enum SaveFieldHolder {

    TEAM("team"),
    MEMBER("player"),
    ;

    private final String tableName;

    SaveFieldHolder(String tableName) {
        this.tableName = name();
    }

    public String getTableName() {
        return tableName;
    }

}
