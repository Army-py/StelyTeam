package fr.army.stelyteam.utils.manager.database.builder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.jetbrains.annotations.Nullable;

import fr.army.stelyteam.utils.manager.database.builder.impl.ISelectQuery;
import fr.army.stelyteam.utils.manager.database.builder.impl.Query;
import fr.army.stelyteam.utils.manager.database.builder.impl.holder.ValueHolder;

public class PreparedSQLRequest {
    
    private final Connection conn;
    private final Query query;

    private PreparedStatement preparedStatement;

    private SQLResult result;

    public PreparedSQLRequest(Connection conn, Query query){
        this.conn = conn;
        this.query = query;
    }


    public PreparedSQLRequest build() throws SQLException{
        preparedStatement = conn.prepareStatement(query.build());
        return this;
    }

    public PreparedSQLRequest execute() throws SQLException{
        if (query instanceof ISelectQuery){
            result = new SQLResult(conn, preparedStatement.executeQuery(), ((ISelectQuery) query).getFields());
        }else{
            preparedStatement.executeUpdate();
        }
        return this;
    }

    public PreparedSQLRequest setValues() throws SQLException{
        if (!(query instanceof ValueHolder)) return this;
        Object[] values = ((ValueHolder) query).getValues().toArray();
        for(int i = 0; i < values.length; i++){
            preparedStatement.setObject(i + 1, values[i]);
        }
        return this;
    }

    public void close() throws SQLException{
        preparedStatement.close();
    }

    @Nullable
    public SQLResult getResult(){
        return result;
    }
}
