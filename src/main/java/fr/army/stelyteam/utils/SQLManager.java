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
                PreparedStatement queryPlayers = connection.prepareStatement("CREATE TABLE IF NOT EXISTS 'player' ('playerId' INTEGER, 'playerName' TEXT, 'teamRank' INTEGER, 'joinDate' TEXT, 'teamId' INTEGER, FOREIGN KEY('teamId') REFERENCES 'team'('teamId'), PRIMARY KEY('playerId' AUTOINCREMENT));");
                PreparedStatement queryTeams = connection.prepareStatement("CREATE TABLE IF NOT EXISTS 'team' ('teamId' INTEGER, 'teamName' TEXT UNIQUE, 'teamPrefix' TEXT, 'teamMoney' INTEGER, 'creationDate' TEXT, 'improvLvlMembers' INTEGER, 'teamStorageLvl' INTEGER, 'unlockedTeamBank' INTEGER, 'teamOwnerPlayerId' INTEGER UNIQUE, PRIMARY KEY('teamId' AUTOINCREMENT), FOREIGN KEY('teamOwnerPlayerId') REFERENCES 'player'('playerId'));");
                PreparedStatement queryTeamStorages = connection.prepareStatement("CREATE TABLE IF NOT EXISTS 'teamStorage' ('storageId' INTEGER, 'teamId' INTEGER, 'storageContent' BLOB, PRIMARY KEY('storageId','teamId'), FOREIGN KEY('teamId') REFERENCES 'team'('teamId'), FOREIGN KEY('storageId') REFERENCES 'storage'('storageId'));");
                PreparedStatement queryAlliances = connection.prepareStatement("CREATE TABLE IF NOT EXISTS 'alliance' ('teamId' INTEGER, 'teamAllianceId' INTEGER, 'allianceDate' TEXT, FOREIGN KEY('teamId') REFERENCES 'team'('teamId'), FOREIGN KEY('teamAllianceId') REFERENCES 'team'('teamId'), PRIMARY KEY('teamId','teamAllianceId'));");
                PreparedStatement queryAssignements = connection.prepareStatement("CREATE TABLE IF NOT EXISTS 'assignement' ('teamId' INTEGER, 'permLabel' TEXT, 'teamRank' INTEGER, FOREIGN KEY('teamId') REFERENCES 'team'('teamId'), PRIMARY KEY('permLabel','teamId'));");
                PreparedStatement queryStorages = connection.prepareStatement("CREATE TABLE IF NOT EXISTS 'storage' ('storageId' INTEGER, PRIMARY KEY('storageId'));");

                queryPlayers.executeUpdate();
                queryTeams.executeUpdate();
                queryTeamStorages.executeUpdate();
                queryAlliances.executeUpdate();
                queryAssignements.executeUpdate();
                queryStorages.executeUpdate();

                queryPlayers.close();
                queryTeams.close();
                queryTeamStorages.close();
                queryAlliances.close();
                queryAssignements.close();
                queryStorages.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public boolean isOwner(String playerName){
        if (isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT * FROM player WHERE playerName = ? AND teamRank = 0");
                query.setString(1, playerName);
                ResultSet result = query.executeQuery();
                boolean isOwner = result.next();
                query.close();
                return isOwner;
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }


    public boolean isMember(String playerName){
        if (isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT * FROM player WHERE playerName = ?");
                query.setString(1, playerName);
                ResultSet result = query.executeQuery();
                boolean isMember = result.next();
                query.close();
                return isMember;
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }


    public boolean isMemberInTeam(String playerName, String teamName){
        if (isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT * FROM player WHERE playerName = ? AND teamId = (SELECT teamId FROM team WHERE teamName = ?)");
                query.setString(1, playerName);
                query.setString(2, teamName);
                ResultSet result = query.executeQuery();
                boolean isMember = result.next();
                query.close();
                return isMember;
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }


    public boolean hasUnlockedTeamBank(String teamName){
        if (isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT * FROM team WHERE teamName = ? AND unlockedTeamBank = 1");
                query.setString(1, teamName);
                ResultSet result = query.executeQuery();
                boolean hasUnlockedTeamBank = result.next();
                query.close();
                return hasUnlockedTeamBank;
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }


    public boolean teamNameExists(String teamName){
        if (isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT * FROM team WHERE teamName = ?");
                query.setString(1, teamName);
                ResultSet result = query.executeQuery();
                boolean teamNameExists = result.next();
                query.close();
                return teamNameExists;
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }


    public boolean teamPrefixExists(String teamPrefix){
        if (isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT * FROM team WHERE teamPrefix = ?");
                query.setString(1, teamPrefix);
                ResultSet result = query.executeQuery();
                boolean teamPrefixExists = result.next();
                query.close();
                return teamPrefixExists;
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }


    public void insertTeam(String teamName, String teamPrefix, String ownerName){
        if(isConnected()){
            try {
                PreparedStatement queryMember = connection.prepareStatement("INSERT INTO player (playerName, teamRank, joinDate) VALUES (?, ?, ?)");
                queryMember.setString(1, ownerName);
                queryMember.setInt(2, 0);
                queryMember.setString(3, getCurrentDate());
                // queryMember.setInt(4, null);
                queryMember.executeUpdate();
                queryMember.close();
                
                PreparedStatement queryTeam = connection.prepareStatement("INSERT INTO team (teamName, teamPrefix, teamMoney, creationDate, improvLvlMembers, teamStorageLvl, unlockedTeamBank, teamOwnerPlayerId) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
                queryTeam.setString(1, teamName);
                queryTeam.setString(2, teamPrefix);
                queryTeam.setInt(3, 0);
                queryTeam.setString(4, getCurrentDate());
                queryTeam.setInt(5, 0);
                queryTeam.setInt(6, 0);
                queryTeam.setInt(7, 0);
                queryTeam.setInt(8, getPlayerId(ownerName));
                queryTeam.executeUpdate();
                queryTeam.close();

                PreparedStatement queryUpdateMember = connection.prepareStatement("UPDATE player SET teamId = (SELECT teamId FROM team WHERE teamName = ?) WHERE playerName = ?");
                queryUpdateMember.setString(1, teamName);
                queryUpdateMember.setString(2, ownerName);
                queryUpdateMember.executeUpdate();
                queryUpdateMember.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void insertMember(String playerName, String teamName){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("INSERT INTO player (playerName, teamRank, joinDate, teamId) VALUES (?, ?, ?, ?)");
                query.setString(1, playerName);
                query.setInt(2, 5);
                query.setString(3, getCurrentDate());
                query.setInt(4, getTeamId(teamName));
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void removeTeam(String teamName){
        if(isConnected()){
            try {
                PreparedStatement queryMembers = connection.prepareStatement("DELETE FROM player WHERE teamId = ?");
                queryMembers.setInt(1, getTeamId(teamName));
                queryMembers.executeUpdate();
                queryMembers.close();

                PreparedStatement queryPermissions = connection.prepareStatement("DELETE FROM assignement WHERE teamId = ?");
                queryPermissions.setInt(1, getTeamId(teamName));
                queryPermissions.executeUpdate();
                queryPermissions.close();

                PreparedStatement queryTeams = connection.prepareStatement("DELETE FROM team WHERE teamName = ?");
                queryTeams.setString(1, teamName);
                queryTeams.executeUpdate();
                queryTeams.close();


            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void removeMember(String playerName, String teamName){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("DELETE FROM player WHERE playerName = ? AND teamId = ?");
                query.setString(1, playerName);
                query.setInt(2, getTeamId(teamName));
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void updateTeamName(String teamName, String newTeamName){
        if(isConnected()){
            try {
                PreparedStatement queryTeam = connection.prepareStatement("UPDATE team SET teamName = ? WHERE teamName = ?");
                queryTeam.setString(1, newTeamName);
                queryTeam.setString(2, teamName);
                queryTeam.executeUpdate();
                queryTeam.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void updateTeamPrefix(String teamName, String newTeamPrefix){
        if(isConnected()){
            try {
                PreparedStatement queryTeam = connection.prepareStatement("UPDATE team SET teamPrefix = ? WHERE teamName = ?");
                queryTeam.setString(1, newTeamPrefix);
                queryTeam.setString(2, teamName);
                queryTeam.executeUpdate();
                queryTeam.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void updateTeamOwner(String teamName, String teamOwner, String newTeamOwner){
        if(isConnected()){
            try {
                PreparedStatement queryOwner = connection.prepareStatement("UPDATE player SET teamRank = ? WHERE teamName = ? AND playerName = ?");
                queryOwner.setInt(1, 1);
                queryOwner.setString(2, teamName);
                queryOwner.setString(3, teamOwner);
                queryOwner.executeUpdate();
                queryOwner.close();

                PreparedStatement queryNewOwner = connection.prepareStatement("UPDATE player SET teamRank = ? WHERE teamName = ? AND playerName = ?");
                queryNewOwner.setInt(1, 0);
                queryNewOwner.setString(2, teamName);
                queryNewOwner.setString(3, newTeamOwner);
                queryNewOwner.executeUpdate();
                queryNewOwner.close();

                PreparedStatement queryTeam = connection.prepareStatement("UPDATE team SET teamOwnerPlayerId = ? WHERE teamName = ? AND teamOwnerPlayerId = ?");
                queryTeam.setString(1, newTeamOwner);
                queryTeam.setString(2, teamName);
                queryTeam.setString(3, teamOwner);
                queryTeam.executeUpdate();
                queryTeam.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void updateUnlockedTeamBank(String teamName){
        if(isConnected()){
            try {
                PreparedStatement queryTeam = connection.prepareStatement("UPDATE team SET unlockedTeamBank = ? WHERE teamName = ?");
                queryTeam.setInt(1, 1);
                queryTeam.setString(2, teamName);
                queryTeam.executeUpdate();
                queryTeam.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void incrementImprovLvlMembers(String teamName){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE team SET improvLvlMembers = improvLvlMembers + 1 WHERE teamName = ?");
                query.setString(1, teamName);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void incrementTeamStorageLvl(String teamName){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE team SET teamStorageLvl = teamStorageLvl + 1 WHERE teamName = ?");
                query.setString(1, teamName);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void incrementTeamMoney(String teamName, double money){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE team SET teamMoney = teamMoney + ? WHERE teamName = ?");
                query.setDouble(1, money);
                query.setString(2, teamName);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void decrementImprovLvlMembers(String teamName){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE team SET improvLvlMembers = improvLvlMembers - 1 WHERE teamName = ?");
                query.setString(1, teamName);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void decrementTeamMoney(String teamName, double money){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE team SET teamMoney = teamMoney - ? WHERE teamName = ?");
                query.setDouble(1, money);
                query.setString(2, teamName);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void promoteMember(String teamName, String playerName){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE player SET teamRank = teamRank - 1 WHERE teamId = ? AND playerName = ?");
                query.setInt(1, getTeamId(teamName));
                query.setString(2, playerName);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void demoteMember(String teamName, String playerName){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE player SET teamRank = teamRank + 1 WHERE teamId = ? AND playerName = ?");
                query.setInt(1, getTeamId(teamName));
                query.setString(2, playerName);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public String getTeamNameFromPlayerName(String playerName){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT t.teamName FROM team AS t INNER JOIN player AS p ON t.teamId = p.teamId WHERE p.playerName = ?");
                query.setString(1, playerName);
                ResultSet result = query.executeQuery();
                if(result.next()){
                    return result.getString("teamName");
                }
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public String getTeamPrefix(String teamName){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT teamPrefix FROM team WHERE teamName = ?");
                query.setString(1, teamName);
                ResultSet result = query.executeQuery();
                if(result.next()){
                    return result.getString("teamPrefix");
                }
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public String getTeamOwnerName(String teamName){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT p.playerName FROM player AS p INNER JOIN team AS t ON p.playerId = t.teamOwnerPlayerId WHERE t.teamName = ?");
                query.setString(1, teamName);
                ResultSet result = query.executeQuery();
                if(result.next()){
                    return result.getString("playerName");
                }
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public Integer getImprovLvlMembers(String teamName){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT improvLvlMembers FROM team WHERE teamName = ?");
                query.setString(1, teamName);
                ResultSet result = query.executeQuery();
                if(result.next()){
                    return result.getInt("improvLvlMembers");
                }
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public Integer getTeamStorageLvl(String teamName){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT teamStorageLvl FROM team WHERE teamName = ?");
                query.setString(1, teamName);
                ResultSet result = query.executeQuery();
                if(result.next()){
                    return result.getInt("teamStorageLvl");
                }
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public Double getTeamMoney(String teamName){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT teamMoney FROM team WHERE teamName = ?");
                query.setString(1, teamName);
                ResultSet result = query.executeQuery();
                if(result.next()){
                    return result.getDouble("teamMoney");
                }
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public ArrayList<String> getTeamMembers(String teamName){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT p.playerName FROM player AS p INNER JOIN team AS t ON p.teamId = t.teamId WHERE t.teamName = ?");
                query.setString(1, teamName);
                ResultSet result = query.executeQuery();
                ArrayList<String> teamMembers = new ArrayList<>();
                while(result.next()){
                    teamMembers.add(result.getString("playerName"));
                }
                query.close();
                return teamMembers;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public ArrayList<String> getTeamMembersWithRank(String teamName, int rank){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT p.playerName FROM player AS p INNER JOIN team AS t ON p.teamId = t.teamId WHERE t.teamName = ? AND p.teamRank <= ? ORDER BY p.teamRank ASC");
                query.setString(1, teamName);
                query.setInt(2, rank);
                ResultSet result = query.executeQuery();
                ArrayList<String> teamMembers = new ArrayList<>();
                while(result.next()){
                    teamMembers.add(result.getString("playerName"));
                }
                query.close();
                return teamMembers;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public ArrayList<String> getTeamsName(){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT teamName FROM team");
                ResultSet result = query.executeQuery();
                ArrayList<String> teamsName = new ArrayList<>();
                while(result.next()){
                    teamsName.add(result.getString("teamName"));
                }
                query.close();
                return teamsName;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public Integer getMemberRank(String playerName){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT teamRank FROM player WHERE playerName = ?");
                query.setString(1, playerName);
                ResultSet result = query.executeQuery();
                if(result.next()){
                    return result.getInt("teamRank");
                }
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public Integer getRankAssignement(String teamName, String permLabel){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT a.teamRank FROM assignement AS a INNER JOIN team AS t ON a.teamId = t.teamId WHERE t.teamName = ? AND a.permLabel = ?");
                query.setString(1, teamName);
                query.setString(2, permLabel);
                ResultSet result = query.executeQuery();
                if(result.next()){
                    return result.getInt("teamRank");
                }
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public String getTeamCreationDate(String teamName){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT creationDate FROM team WHERE teamName = ?");
                query.setString(1, teamName);
                ResultSet result = query.executeQuery();
                if(result.next()){
                    return result.getString("creationDate");
                }
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public void insertAssignement(String teamName, String permLabel, Integer teamRank){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("INSERT INTO assignement (teamId, permLabel, teamRank) VALUES (?, ?, ?)");
                query.setInt(1, getTeamId(teamName));
                query.setString(2, permLabel);
                query.setInt(3, teamRank);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void incrementAssignement(String teamName, String permLabel){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE assignement SET teamRank = teamRank + 1 WHERE teamId = ? AND permLabel = ?");
                query.setInt(1, getTeamId(teamName));
                query.setString(2, permLabel);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void decrementAssignement(String teamName, String permLabel){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE assignement SET teamRank = teamRank - 1 WHERE teamId = ? AND permLabel = ?");
                query.setInt(1, getTeamId(teamName));
                query.setString(2, permLabel);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public String getJoinDate(String playerName){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT joinDate FROM player WHERE playerName = ?");
                query.setString(1, playerName);
                ResultSet result = query.executeQuery();
                if(result.next()){
                    return result.getString("joinDate");
                }
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public void insertStorage(int storageId){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("INSERT INTO storage (storageId) VALUES (?)");
                query.setInt(1, storageId);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public boolean storageExist(int storageId){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT storageId FROM storage WHERE storageId = ?");
                query.setInt(1, storageId);
                ResultSet result = query.executeQuery();
                if(result.next()){
                    return true;
                }
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public boolean teamHasStorage(String teamName, Integer storageId){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT * FROM teamStorage AS ts INNER JOIN team AS t ON ts.teamId = t.teamId WHERE t.teamName = ? AND ts.storageId = ?");
                query.setString(1, teamName);
                query.setInt(2, storageId);
                ResultSet result = query.executeQuery();
                boolean isParticipant = result.next();
                query.close();
                return isParticipant;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public String getStorageContent(String teamName, Integer storageId){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT storageContent FROM teamStorage AS ts INNER JOIN team AS t ON ts.teamId = t.teamId WHERE t.teamName = ? AND ts.storageId = ?");
                query.setString(1, teamName);
                query.setInt(2, storageId);
                ResultSet result = query.executeQuery();
                if(result.next()){
                    return result.getString("storageContent");
                }
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public void insertStorageContent(String teamName, Integer storageId, String storageContent){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("INSERT INTO teamStorage VALUES (?, ?, ?)");
                query.setInt(1, storageId);
                query.setInt(2, getTeamId(teamName));
                query.setString(3, storageContent);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void updateStorageContent(String teamName, Integer storageId, String storageContent){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE teamStorage SET storageContent = ? WHERE teamId = ? AND storageId = ?");
                query.setString(1, storageContent);
                query.setInt(2, getTeamId(teamName));
                query.setInt(3, storageId);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    private int getPlayerId(String playerName){
        if (isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT playerId FROM player WHERE playerName = ?");
                query.setString(1, playerName);
                ResultSet result = query.executeQuery();
                int playerId = result.getInt("playerId");
                query.close();
                return playerId;
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return 0;
    }


    public ArrayList<String> getAlliances(String teamName){
        if(isConnected()){
            try {
                ArrayList<String> alliances = new ArrayList<>();
                PreparedStatement queryTeam = connection.prepareStatement("SELECT t.teamName FROM team AS t INNER JOIN alliance AS a ON t.teamId = a.teamAllianceId WHERE a.teamId = ?");
                queryTeam.setInt(1, getTeamId(teamName));
                ResultSet resultTeam = queryTeam.executeQuery();
                while(resultTeam.next()){
                    alliances.add(resultTeam.getString("teamName"));
                }
                queryTeam.close();
                
                PreparedStatement queryAlliance = connection.prepareStatement("SELECT t.teamName FROM team AS t INNER JOIN alliance AS a ON t.teamId = a.teamId WHERE a.teamAllianceId = ?");
                queryAlliance.setInt(1, getTeamId(teamName));
                ResultSet resultAlliance = queryAlliance.executeQuery();
                while(resultAlliance.next()){
                    alliances.add(resultAlliance.getString("teamName"));
                }
                queryAlliance.close();
                return alliances;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public String getAllianceDate(String teamName, String allianceName){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT a.allianceDate FROM alliance AS a INNER JOIN team AS t ON a.teamId = t.teamId WHERE (t.teamName = ? OR t.teamName = ?) AND (a.teamAllianceId = ? OR a.teamId = ?)");
                query.setString(1, teamName);
                query.setString(2, allianceName);
                query.setInt(3, getTeamId(allianceName));
                query.setInt(4, getTeamId(allianceName));
                ResultSet result = query.executeQuery();
                if(result.next()){
                    return result.getString("allianceDate");
                }
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public void insertAlliance(String teamName, String allianceName){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("INSERT INTO alliance VALUES (?, ?, ?)");
                query.setInt(1, getTeamId(teamName));
                query.setInt(2, getTeamId(allianceName));
                query.setString(3, getCurrentDate());
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void removeAlliance(String teamName, String allianceName){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("DELETE FROM alliance WHERE (teamId = ? AND teamAllianceId = ?) OR (teamId = ? AND teamAllianceId = ?)");
                query.setInt(1, getTeamId(teamName));
                query.setInt(2, getTeamId(allianceName));
                query.setInt(3, getTeamId(allianceName));
                query.setInt(4, getTeamId(teamName));
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public boolean isAlliance(String teamName, String allianceName){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT * FROM alliance AS a INNER JOIN team AS t ON a.teamId = t.teamId WHERE (t.teamName = ? OR t.teamName = ?) AND (a.teamAllianceId = ? OR a.teamId = ?)");
                query.setString(1, teamName);
                query.setString(2, allianceName);
                query.setInt(3, getTeamId(allianceName));
                query.setInt(4, getTeamId(allianceName));
                ResultSet result = query.executeQuery();
                if(result.next()){
                    return true;
                }
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    private int getTeamId(String teamName){
        if (isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT teamId FROM team WHERE teamName = ?");
                query.setString(1, teamName);
                ResultSet result = query.executeQuery();
                int teamId = 0;
                if(result.next()){
                    teamId = result.getInt("teamId");
                }
                query.close();
                return teamId;
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return 0;
    }


    private String getCurrentDate(){
        return new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
    }
}