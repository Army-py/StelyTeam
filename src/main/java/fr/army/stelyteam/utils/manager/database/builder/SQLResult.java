package fr.army.stelyteam.utils.manager.database.builder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;

import fr.army.stelyteam.cache.SaveField;
import fr.army.stelyteam.team.BankAccountSnapShot;
import fr.army.stelyteam.team.TeamSnapshot;
import fr.army.stelyteam.utils.manager.database.builder.impl.query.SelectQuery;

public class SQLResult {
    
    private final Connection conn;
    private final ResultSet result;
    private final SaveField[] fields;

    public SQLResult(Connection conn, ResultSet result, SaveField... fields){
        this.conn = conn;
        this.result = result;
        this.fields = fields;
    }


    public Map<SaveField, Optional<String>> getStrings() throws SQLException{
        Map<SaveField, Optional<String>> map = new HashMap<>();
        for(SaveField field : fields){
            // replace field.getColumnName() by the method to get the column name
            if (result.next()){
                map.put(field, Optional.ofNullable(result.getString(field.getColumnName())));
            }
        }
        result.close();
        return map;
    }

    public Optional<String> getString() throws SQLException{
        Optional<String> optional = Optional.empty();

        if (result.next()){
            optional = Optional.of(result.getString(fields[0].getColumnName()));
        }
        result.close();
        return optional;
    }

    public Optional<UUID> getUUID() throws SQLException{
        Optional<UUID> optional = Optional.empty();

        if (result.next()){
            optional = Optional.of(UUID.fromString(result.getString(fields[0].getColumnName())));
        }
        result.close();
        return optional;
    }

    public List<UUID> getUUIDs() throws SQLException{
        List<UUID> list = new ArrayList<>();

        while (result.next()){
            list.add(UUID.fromString(result.getString(fields[0].getColumnName())));
        }
        result.close();
        return list;
    }

    public TeamSnapshot getTeamSnapshot() throws SQLException{
        TeamSnapshot team = null;

        if (result.next()){
            UUID teamUuid = UUID.fromString(result.getString(SaveField.TEAM_UUID.getColumnName()));
            UUID ownerUuid = UUID.fromString(result.getString(SaveField.MEMBER_UUID.getColumnName()));
            BankAccountSnapShot bank = new BankAccountSnapShot(
                Optional.of(result.getBoolean(SaveField.BANK_UNLOCKED.getColumnName())),
                Optional.of(result.getDouble(SaveField.BANK_BALANCE.getColumnName()))
            );

            SelectQuery selectMembers = new SelectQuery();
            selectMembers.setTables("player", "team");
            selectMembers.setFields(SaveField.MEMBER_UUID);
            selectMembers.setConditions("player.team_id = team.team_id", "team.team_uuid = '" + teamUuid.toString() + "'");
            PreparedSQLRequest request = new PreparedSQLRequest(conn, selectMembers);
            request.build().execute();
            List<UUID> members = request.getResult().getUUIDs();

            team = new TeamSnapshot(
                teamUuid, 
                Optional.of(result.getString(SaveField.NAME.getColumnName())), 
                Optional.of(result.getString(SaveField.PREFIX.getColumnName())), 
                Optional.of(result.getString(SaveField.DESCRIPTION.getColumnName())),
                Optional.of(Date.from(result.getTimestamp(SaveField.CREATION_DATE.getColumnName()).toInstant())),
                Optional.of(bank),
                Optional.of(Bukkit.getOfflinePlayer(ownerUuid).getName()),
                Optional.of(members));
        }
        
        result.close();
        return team;
    }

}
