package fr.army.stelyteam.utils.manager.database.builder;

import java.sql.ResultSet;

public class SQLResult {
    
    private final ResultSet resultSet;

    public SQLResult(ResultSet resultSet){
        this.resultSet = resultSet;
    }
}
