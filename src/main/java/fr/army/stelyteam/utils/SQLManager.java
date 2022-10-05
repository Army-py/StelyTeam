package fr.army.stelyteam.utils;

import java.io.InputStream;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import fr.army.stelyteam.StelyTeamPlugin;

public class SQLManager {
    
    private String host;
    private String database;
    private String user;
    private String password;
    private int port;

    private Connection connection;
    private StelyTeamPlugin plugin;

    public SQLManager(StelyTeamPlugin plugin) {
        // this.host = App.config.getString("sql.host");
        // this.database = App.config.getString("sql.database");
        // this.user = App.config.getString("sql.user");
        // this.password = App.config.getString("sql.password");
        // this.port = App.config.getInt("sql.port");

        this.database = "bungeecord_StelyTeam.db";

        this.plugin = plugin;
    }


    public boolean isConnected() {
        return connection == null ? false : true;
    }


    public void connect() throws ClassNotFoundException, SQLException{
        if(!isConnected()){
            this.connection = DriverManager.getConnection("jdbc:sqlite:"+ plugin.getDataFolder().getAbsolutePath()+"/"+this.database);
        }
    }


    public void disconnect() {
        if(isConnected()){
            try {
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public Connection getConnection() {
        return connection;
    }


    public void disconnect(Connection connection) {
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void createTables(){
        if (isConnected()){
            try {
                PreparedStatement queryPlayers = connection.prepareStatement("CREATE TABLE IF NOT EXISTS 'players' ('id' INTEGER UNIQUE, 'playername' TEXT UNIQUE, 'rank' INTEGER, 'team_id' INTEGER, 'join_date' TEXT, PRIMARY KEY('id' AUTOINCREMENT), FOREIGN KEY('team_id') REFERENCES 'teams'('id'));");
                PreparedStatement queryTeams = connection.prepareStatement("CREATE TABLE IF NOT EXISTS 'teams' ('id' INTEGER UNIQUE, 'team_id' TEXT UNIQUE, 'team_prefix' TEXT UNIQUE, 'owner' TEXT, 'money' INTEGER, 'creation_date' TEXT, 'members_level' INTEGER, 'team_bank' INTEGER, 'team_storage' INTEGER, PRIMARY KEY('id' AUTOINCREMENT), FOREIGN KEY('owner') REFERENCES 'players'('playername'));");
                PreparedStatement queryPermissions = connection.prepareStatement("CREATE TABLE IF NOT EXISTS 'permissions' ( 'id' INTEGER, 'team_id' INTEGER, 'permission' TEXT, 'rank' INTEGER, FOREIGN KEY('team_id') REFERENCES 'teams'('id'), PRIMARY KEY('id'));");
                PreparedStatement queryStorages = connection.prepareStatement("CREATE TABLE IF NOT EXISTS 'storages' ( 'id' INTEGER, 'team_id' INTEGER, 'storage_id' INTEGER, 'content' BLOB, FOREIGN KEY('team_id') REFERENCES 'teams'('id'), PRIMARY KEY('id'));");
                PreparedStatement queryAlliances = connection.prepareStatement("CREATE TABLE IF NOT EXISTS 'alliances' ('id' INTEGER, 'team_id' INTEGER, 'alliance_id' INTEGER, 'alliance_date' TEXT, PRIMARY KEY('id' AUTOINCREMENT));");
                
                queryPlayers.executeUpdate();
                queryTeams.executeUpdate();
                queryPermissions.executeUpdate();
                queryStorages.executeUpdate();
                queryAlliances.executeUpdate();

                queryPlayers.close();
                queryTeams.close();
                queryPermissions.close();
                queryStorages.close();
                queryAlliances.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public boolean isOwner(String playername){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT playername FROM players WHERE rank = 0 AND playername = ?");
                query.setString(1, playername);
                ResultSet result = query.executeQuery();
                boolean isParticipant = result.next();
                query.close();
                return isParticipant;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public boolean isMember(String playername){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT playername FROM players WHERE playername = ?");
                query.setString(1, playername);
                ResultSet result = query.executeQuery();
                boolean isParticipant = result.next();
                query.close();
                return isParticipant;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public boolean isMemberInTeam(String playername, String teamId){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT playername FROM players WHERE playername = ? AND team_id = ?");
                query.setString(1, playername);
                query.setInt(2, getTeamId(teamId));
                ResultSet result = query.executeQuery();
                boolean isParticipant = result.next();
                query.close();
                return isParticipant;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public boolean hasUnlockedTeamBank(String teamId){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT team_id FROM teams WHERE team_id = ? AND team_bank = 1");
                query.setInt(1, getTeamId(teamId));
                ResultSet result = query.executeQuery();
                boolean isParticipant = result.next();
                query.close();
                return isParticipant;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public boolean teamIdExist(String teamId){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT team_id FROM teams WHERE team_id = ?");
                query.setString(1, teamId);
                ResultSet result = query.executeQuery();
                boolean isParticipant = result.next();
                query.close();
                return isParticipant;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public boolean teamPrefixExist(String teamPrefix){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT team_prefix FROM teams WHERE team_prefix = ?");
                query.setString(1, teamPrefix);
                ResultSet result = query.executeQuery();
                boolean isParticipant = result.next();
                query.close();
                return isParticipant;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public void insertTeam(String teamID, String teamPrefix, String owner){
        if(isConnected()){
            try {
                PreparedStatement queryTeam = connection.prepareStatement("INSERT INTO teams (team_id, team_prefix, owner, money, creation_date, members_level, team_bank, team_storage) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
                queryTeam.setString(1, teamID);
                queryTeam.setString(2, teamPrefix);
                queryTeam.setString(3, owner);
                queryTeam.setInt(4, 0);
                queryTeam.setString(5, getCurrentDate());
                queryTeam.setInt(6, 0);
                queryTeam.setInt(7, 0);
                queryTeam.setInt(8, 0);
                queryTeam.executeUpdate();
                queryTeam.close();

                PreparedStatement queryMember = connection.prepareStatement("INSERT INTO players (playername, rank, team_id, join_date) VALUES (?, ?, ?, ?)");
                queryMember.setString(1, owner);
                queryMember.setInt(2, 0);
                queryMember.setInt(3, getTeamId(teamID));
                queryMember.setString(4, getCurrentDate());
                queryMember.executeUpdate();
                queryMember.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void insertMember(String playername, String teamId){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("INSERT INTO players (playername, rank, team_id, join_date) VALUES (?, ?, ?, ?)");
                query.setString(1, playername);
                query.setInt(2, 5);
                query.setInt(3, getTeamId(teamId));
                query.setString(4, getCurrentDate());
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void removeTeam(String teamId){
        if(isConnected()){
            try {
                PreparedStatement queryTeams = connection.prepareStatement("DELETE FROM teams WHERE team_id = ?");
                queryTeams.setString(1, teamId);
                queryTeams.executeUpdate();
                queryTeams.close();

                PreparedStatement queryMembers = connection.prepareStatement("DELETE FROM players WHERE team_id = ?");
                queryMembers.setInt(1, getTeamId(teamId));
                queryMembers.executeUpdate();
                queryMembers.close();

                PreparedStatement queryPermissions = connection.prepareStatement("DELETE FROM permissions WHERE team_id = ?");
                queryPermissions.setInt(1, getTeamId(teamId));
                queryPermissions.executeUpdate();
                queryPermissions.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void removeMember(String playername, String teamId){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("DELETE FROM players WHERE playername = ? AND team_id = ?");
                query.setString(1, playername);
                query.setInt(2, getTeamId(teamId));
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void updateTeamID(String teamId, String newTeamId){
        if(isConnected()){
            try {
                PreparedStatement queryTeam = connection.prepareStatement("UPDATE teams SET team_id = ? WHERE team_id = ?");
                queryTeam.setString(1, newTeamId);
                queryTeam.setString(2, teamId);
                queryTeam.executeUpdate();
                queryTeam.close();

                PreparedStatement queryMember = connection.prepareStatement("UPDATE players SET team_id = ? WHERE team_id = ?");
                queryMember.setString(1, newTeamId);
                queryMember.setInt(2, getTeamId(teamId));
                queryMember.executeUpdate();
                queryMember.close();

                PreparedStatement queryPermissions = connection.prepareStatement("UPDATE permissions SET team_id = ? WHERE team_id = ?");
                queryPermissions.setString(1, newTeamId);
                queryPermissions.setInt(2, getTeamId(teamId));
                queryPermissions.executeUpdate();
                queryPermissions.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void updateTeamPrefix(String teamId, String newTeamPrefix){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE teams SET team_prefix = ? WHERE team_id = ?");
                query.setString(1, newTeamPrefix);
                query.setString(2, teamId);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void updateTeamOwner(String teamId, String newOwner, String owner){
        if(isConnected()){
            try {
                PreparedStatement queryOwner = connection.prepareStatement("UPDATE players SET rank = ? WHERE team_id = ? AND playername = ?");
                queryOwner.setInt(1, 1);
                queryOwner.setInt(2, getTeamId(teamId));
                queryOwner.setString(3, owner);
                queryOwner.executeUpdate();
                queryOwner.close();

                PreparedStatement queryNewOwner = connection.prepareStatement("UPDATE players SET rank = ? WHERE team_id = ? AND playername = ?");
                queryNewOwner.setInt(1, 0);
                queryNewOwner.setInt(2, getTeamId(teamId));
                queryNewOwner.setString(3, newOwner);
                queryNewOwner.executeUpdate();
                queryNewOwner.close();

                PreparedStatement queryTeam = connection.prepareStatement("UPDATE teams SET owner = ? WHERE team_id = ? AND owner = ?");
                queryTeam.setString(1, newOwner);
                queryTeam.setString(2, teamId);
                queryTeam.setString(3, owner);
                queryTeam.executeUpdate();
                queryTeam.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void updateUnlockTeamBank(String teamId){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE teams SET team_bank = ? WHERE team_id = ?");
                query.setInt(1, 1);
                query.setString(2, teamId);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void incrementTeamLevel(String teamId){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE teams SET members_level = members_level + 1 WHERE team_id = ?");
                query.setString(1, teamId);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void incrementTeamStorage(String teamId){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE teams SET team_storage = team_storage + 1 WHERE team_id = ?");
                query.setString(1, teamId);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void incrementTeamMoney(String teamId, double money){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE teams SET money = money + ? WHERE team_id = ?");
                query.setDouble(1, money);
                query.setString(2, teamId);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void decrementTeamLevel(String teamId){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE teams SET members_level = members_level - 1 WHERE team_id = ?");
                query.setString(1, teamId);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void decrementTeamMoney(String teamId, double money){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE teams SET money = money - ? WHERE team_id = ?");
                query.setDouble(1, money);
                query.setString(2, teamId);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void promoteMember(String teamId, String playername){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE players SET rank = rank - 1 WHERE team_id = ? AND playername = ?");
                query.setInt(1, getTeamId(teamId));
                query.setString(2, playername);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void demoteMember(String teamId, String playername){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE players SET rank = rank + 1 WHERE team_id = ? AND playername = ?");
                query.setInt(1, getTeamId(teamId));
                query.setString(2, playername);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public String getTeamIDFromPlayer(String playername){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT teams.team_id FROM teams INNER JOIN players ON teams.id = players.team_id WHERE players.playername = ?");
                query.setString(1, playername);
                ResultSet result = query.executeQuery();
                String teamID = null;
                if(result.next()){
                    teamID = result.getString("team_id");
                }
                query.close();
                return teamID;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public String getTeamPrefix(String teamID){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT team_prefix FROM teams WHERE team_id = ?");
                query.setString(1, teamID);
                ResultSet result = query.executeQuery();
                String teamPrefix = null;
                if(result.next()){
                    teamPrefix = result.getString("team_prefix");
                }
                query.close();
                return teamPrefix;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public String getTeamOwner(String teamId){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT owner FROM teams WHERE team_id = ?");
                query.setString(1, teamId);
                ResultSet result = query.executeQuery();
                String teamOwner = null;
                if(result.next()){
                    teamOwner = result.getString("owner");
                }
                query.close();
                return teamOwner;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public Integer getTeamMembersLevel(String teamId){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT members_level FROM teams WHERE team_id = ?");
                query.setString(1, teamId);
                ResultSet result = query.executeQuery();
                Integer level = null;
                if(result.next()){
                    level = result.getInt("members_level");
                }
                query.close();
                return level;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public Integer getTeamStorageLevel(String teamId){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT team_storage FROM teams WHERE team_id = ?");
                query.setString(1, teamId);
                ResultSet result = query.executeQuery();
                Integer storage = null;
                if(result.next()){
                    storage = result.getInt("team_storage");
                }
                query.close();
                return storage;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public Double getTeamMoney(String teamId){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT money FROM teams WHERE team_id = ?");
                query.setString(1, teamId);
                ResultSet result = query.executeQuery();
                Double money = null;
                if(result.next()){
                    money = result.getDouble("money");
                }
                query.close();
                return money;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public ArrayList<String> getMembers(String teamId){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT playername FROM players WHERE team_id = ? ORDER BY rank, playername");
                query.setInt(1, getTeamId(teamId));

                ResultSet result = query.executeQuery();
                ArrayList<String> data = new ArrayList<String>();
                while(result.next()){
                    data.add(result.getString("playername"));
                }
                query.close();
                return data;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public ArrayList<String> getTeamsIds(){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT team_id FROM teams");
                ResultSet result = query.executeQuery();
                ArrayList<String> data = new ArrayList<String>();
                while(result.next()){
                    data.add(result.getString("team_id"));
                }
                query.close();
                return data;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public Integer getMemberRank(String playerName) {
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT rank FROM players WHERE playername = ?");
                query.setString(1, playerName);
                ResultSet result = query.executeQuery();
                Integer rank = null;
                if(result.next()){
                    rank = result.getInt("rank");
                }
                query.close();
                return rank;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public Integer getPermissionRank(String teamId, String permission){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT rank FROM permissions WHERE team_id = ? AND permission = ?");
                query.setInt(1, getTeamId(teamId));
                query.setString(2, permission);
                ResultSet result = query.executeQuery();
                Integer rank = null;
                if(result.next()){
                    rank = result.getInt("rank");
                }
                query.close();
                return rank;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public String getCreationDate(String teamId){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT creation_date FROM teams WHERE team_id = ?");
                query.setString(1, teamId);
                ResultSet result = query.executeQuery();
                String creationDate = null;
                if(result.next()){
                    creationDate = result.getString("creation_date");
                }
                query.close();
                return creationDate;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public void insertPermission(String teamId, String permission, Integer rank){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("INSERT INTO permissions (team_id, permission, rank) VALUES (?, ?, ?)");
                query.setInt(1, getTeamId(teamId));
                query.setString(2, permission);
                query.setInt(3, rank);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void promoteRankPermission(String teamId, String permission){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE permissions SET rank = rank - 1 WHERE team_id = ? AND permission = ?");
                query.setInt(1, getTeamId(teamId));
                query.setString(2, permission);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void demoteRankPermission(String teamId, String permission){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE permissions SET rank = rank + 1 WHERE team_id = ? AND permission = ?");
                query.setInt(1, getTeamId(teamId));
                query.setString(2, permission);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public String getJoinDate(String playername) {
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT join_date FROM players WHERE playername = ?");
                query.setString(1, playername);
                ResultSet result = query.executeQuery();
                String joinDate = null;
                if(result.next()){
                    joinDate = result.getString("join_date");
                }
                query.close();
                return joinDate;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public boolean teamHasStorage(String teamId, String storageId){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT * FROM storages WHERE team_id = ? AND storage_id = ?");
                query.setInt(1, getTeamId(teamId));
                query.setString(2, storageId);
                ResultSet result = query.executeQuery();
                if(result.next()){
                    query.close();
                    return true;
                }
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public String getStorageContent(String teamId, String storageId){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT content FROM storages WHERE team_id = ? AND storage_id = ?");
                query.setInt(1, getTeamId(teamId));
                query.setString(2, storageId);
                ResultSet result = query.executeQuery();
                String content = null;
                if(result.next()){
                    content = (String) result.getObject("content");
                }
                query.close();
                return content;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public void insertStorageContent(String teamId, String storageId, String content){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("INSERT INTO storages (team_id, storage_id, content) VALUES (?, ?, ?)");
                query.setInt(1, getTeamId(teamId));
                query.setString(2, storageId);
                query.setObject(3, content);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void updateStorageContent(String teamId, String storageId, String content){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE storages SET content = ? WHERE team_id = ? AND storage_id = ?");
                query.setObject(1, content);
                query.setInt(2, getTeamId(teamId));
                query.setString(3, storageId);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public ArrayList<String> getAlliances(String teamId){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT team_id FROM alliances WHERE team_id = ? OR allied_team_id = ?");
                query.setInt(1, getTeamId(teamId));
                query.setInt(2, getTeamId(teamId));

                ResultSet result = query.executeQuery();
                ArrayList<String> data = new ArrayList<String>();
                while(result.next()){
                    data.add(result.getString("team_id"));
                }
                query.close();
                return data;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public void insertAlliance(String teamId, String allianceId){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("INSERT INTO alliances (team_id, alliance_id, alliance_date) VALUES (?, ?, ?)");
                query.setInt(1, getTeamId(teamId));
                query.setInt(2, getTeamId(allianceId));
                query.setString(3, getCurrentDate());
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void removeAlliance(String teamId, String allianceId){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("DELETE FROM alliances WHERE team_id = ? AND alliance_id = ?");
                query.setInt(1, getTeamId(teamId));
                query.setInt(2, getTeamId(allianceId));
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public boolean isAlliance(String teamId, String allianceId){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT * FROM alliances WHERE team_id = ? AND alliance_id = ?");
                query.setInt(1, getTeamId(teamId));
                query.setInt(2, getTeamId(allianceId));
                ResultSet result = query.executeQuery();
                if(result.next()){
                    query.close();
                    return true;
                }
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return isAlliance(allianceId, teamId);
    }



    private String getCurrentDate(){
        return new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
    }


    private Integer getTeamId(String teamId) {
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT id FROM teams WHERE team_id = ?");
                query.setString(1, teamId);
                ResultSet result = query.executeQuery();
                Integer team_id = null;
                if(result.next()){
                    team_id = result.getInt("id");
                }
                query.close();
                return team_id;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}