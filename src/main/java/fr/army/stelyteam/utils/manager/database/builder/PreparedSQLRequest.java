package fr.army.stelyteam.utils.manager.database.builder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PreparedSQLRequest<T> {
    
    private final Connection conn;
    private final FundamentalOperator fundamentalOperator;

    private PreparedStatement preparedStatement;

    private T result;

    public PreparedSQLRequest(Connection conn, FundamentalOperator fundamentalOperator){
        this.conn = conn;
        this.fundamentalOperator = fundamentalOperator;
    }


    public PreparedSQLRequest<T> build() throws SQLException{
        preparedStatement = conn.prepareStatement(fundamentalOperator.build());
        return this;
    }

    public PreparedSQLRequest<T> execute() throws SQLException{
        ResultSet result = preparedStatement.executeQuery();
        return this;
    }
}
