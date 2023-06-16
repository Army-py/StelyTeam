package fr.army.stelyteam.utils.manager.database.builder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PreparedSQLRequest {
    
    private final Connection conn;
    private final FundamentalOperator fundamentalOperator;

    private PreparedStatement preparedStatement;

    private SQLResult result;

    public PreparedSQLRequest(Connection conn, FundamentalOperator fundamentalOperator){
        this.conn = conn;
        this.fundamentalOperator = fundamentalOperator;
    }


    public PreparedSQLRequest build() throws SQLException{
        preparedStatement = conn.prepareStatement(fundamentalOperator.build());
        return this;
    }

    public PreparedSQLRequest execute() throws SQLException{
        result = new SQLResult(conn, preparedStatement.executeQuery(), fundamentalOperator.getColumns());
        return this;
    }

    public void close() throws SQLException{
        preparedStatement.close();
    }

    public SQLResult getResult(){
        return result;
    }
}
