package fr.army.stelyteam.utils.manager.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.Alliance;
import fr.army.stelyteam.utils.Member;
import fr.army.stelyteam.utils.Permission;
import fr.army.stelyteam.utils.Storage;
import fr.army.stelyteam.utils.Team;

public class MySQLManager extends DatabaseManager {
    
    private String host;
    private String database;
    private String user;
    private String password;

    private Connection connection;
    private YamlConfiguration config;

    public MySQLManager(StelyTeamPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
        this.config = plugin.getConfig();

        this.host = this.config.getString("sql.host");
        this.database = this.config.getString("sql.database");
        this.user = this.config.getString("sql.user");
        this.password = this.config.getString("sql.password");
    }

    @Override
    public boolean isConnected() {
        return this.connection == null ? false : true;
    }

    @Override
    public void init() throws ClassNotFoundException, SQLException{
        if(!isConnected()){
            this.connection = DriverManager.getConnection("jdbc:mysql://"+this.host+"/"+this.database, this.user, this.password);
        }
    }

    @Override
    public void disconnect() {
        if(isConnected()){
            try {
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }


    @Override
    public void createTables(){
        if (isConnected()){
            try {
                PreparedStatement queryCreatePlayers = connection.prepareStatement("CREATE TABLE IF NOT EXISTS player (playerId INTEGER AUTO_INCREMENT, playerName VARCHAR(255), teamRank INTEGER, joinDate VARCHAR(255), teamId INTEGER, PRIMARY KEY (playerId));");
                PreparedStatement queryCreateTeams = connection.prepareStatement("CREATE TABLE IF NOT EXISTS team (teamId INTEGER AUTO_INCREMENT, teamUuid VARCHAR(255) UNIQUE, teamName VARCHAR(255) UNIQUE, teamPrefix VARCHAR(255), teamDescription VARCHAR(255), teamMoney INTEGER, creationDate VARCHAR(255), improvLvlMembers INTEGER, teamStorageLvl INTEGER, unlockedTeamBank INTEGER, teamOwnerPlayerId INTEGER UNIQUE, PRIMARY KEY(teamId));");
                PreparedStatement queryCreateTeamStorages = connection.prepareStatement("CREATE TABLE IF NOT EXISTS teamStorage (storageId INTEGER, teamId INTEGER, storageContent BLOB, PRIMARY KEY (storageId,teamId));");
                PreparedStatement queryCreateAlliances = connection.prepareStatement("CREATE TABLE IF NOT EXISTS alliance (teamId INTEGER, teamAllianceId INTEGER, allianceDate VARCHAR(255), PRIMARY KEY(teamId,teamAllianceId));");
                PreparedStatement queryCreateAssignements = connection.prepareStatement("CREATE TABLE IF NOT EXISTS assignement (teamId INTEGER, permLabel VARCHAR(255), teamRank INTEGER, PRIMARY KEY (permLabel,teamId));");
                PreparedStatement queryCreateStorages = connection.prepareStatement("CREATE TABLE IF NOT EXISTS storage (storageId INTEGER, PRIMARY KEY (storageId));");

                queryCreatePlayers.executeUpdate();
                queryCreateTeams.executeUpdate();
                queryCreateTeamStorages.executeUpdate();
                queryCreateAlliances.executeUpdate();
                queryCreateAssignements.executeUpdate();
                queryCreateStorages.executeUpdate();

                queryCreatePlayers.close();
                queryCreateTeams.close();
                queryCreateTeamStorages.close();
                queryCreateAlliances.close();
                queryCreateAssignements.close();
                queryCreateStorages.close();


                PreparedStatement queryAlterPlayers = connection.prepareStatement("ALTER TABLE player ADD FOREIGN KEY (teamId) REFERENCES team(teamId);");
                PreparedStatement queryAlterTeams = connection.prepareStatement("ALTER TABLE team ADD FOREIGN KEY (teamOwnerPlayerId) REFERENCES player(playerId);");
                PreparedStatement queryAlterTeamStorages = connection.prepareStatement("ALTER TABLE teamStorage ADD FOREIGN KEY (teamId) REFERENCES team (teamId);");
                PreparedStatement queryAlterTeamStorages2 = connection.prepareStatement("ALTER TABLE teamStorage ADD FOREIGN KEY (storageId) REFERENCES storage(storageId);");
                PreparedStatement queryAlterAlliances = connection.prepareStatement("ALTER TABLE alliance ADD FOREIGN KEY (teamId) REFERENCES team(teamId);");
                PreparedStatement queryAlterAlliances2 = connection.prepareStatement("ALTER TABLE alliance ADD FOREIGN KEY (teamAllianceId) REFERENCES team(teamId);");
                PreparedStatement queryAlterAssignements = connection.prepareStatement("ALTER TABLE assignement ADD FOREIGN KEY (teamId) REFERENCES team(teamId);");
                
                queryAlterPlayers.executeUpdate();
                queryAlterTeams.executeUpdate();
                queryAlterTeamStorages.executeUpdate();
                queryAlterTeamStorages2.executeUpdate();
                queryAlterAlliances.executeUpdate();
                queryAlterAlliances2.executeUpdate();
                queryAlterAssignements.executeUpdate();

                queryAlterPlayers.close();
                queryAlterTeams.close();
                queryAlterTeamStorages.close();
                queryAlterTeamStorages2.close();
                queryAlterAlliances.close();
                queryAlterAlliances2.close();
                queryAlterAssignements.close();

                PreparedStatement queryIfColumnExists = connection.prepareStatement("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'team' AND COLUMN_NAME = 'teamUuid'");
                ResultSet result = queryIfColumnExists.executeQuery();
                if(!result.next()){
                    PreparedStatement queryAlterTeams2 = connection.prepareStatement("ALTER TABLE team ADD teamUuid VARCHAR(255) UNIQUE AFTER teamId");
                    queryAlterTeams2.executeUpdate();
                    queryAlterTeams2.close();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public void insertTeam(String teamName, String teamPrefix, String ownerName){
        if(isConnected()){
            try {
                PreparedStatement queryMember = connection.prepareStatement("INSERT INTO player (playerName, teamRank, joinDate) VALUES (?, ?, ?)");
                queryMember.setString(1, ownerName);
                queryMember.setInt(2, 0);
                queryMember.setString(3, getCurrentDate());
                queryMember.executeUpdate();
                queryMember.close();
                
                PreparedStatement queryTeam = connection.prepareStatement("INSERT INTO team (teamName, teamPrefix, teamDescription, teamMoney, creationDate, improvLvlMembers, teamStorageLvl, unlockedTeamBank, teamOwnerPlayerId) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                queryTeam.setString(1, teamName);
                queryTeam.setString(2, teamPrefix);
                queryTeam.setString(3, config.getString("teamDefaultDescription"));
                queryTeam.setInt(4, 0);
                queryTeam.setString(5, getCurrentDate());
                queryTeam.setInt(6, 0);
                queryTeam.setInt(7, 0);
                queryTeam.setInt(8, 0);
                queryTeam.setInt(9, getPlayerId(ownerName));
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

    @Override
    public void insertMember(String playerName, String teamName){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("INSERT INTO player (playerName, teamRank, joinDate, teamId) VALUES (?, ?, ?, ?)");
                query.setString(1, playerName);
                query.setInt(2, plugin.getLastRank());
                query.setString(3, getCurrentDate());
                query.setInt(4, getTeamId(teamName));
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void removeTeam(String teamName){
        if(isConnected()){
            try {
                PreparedStatement queryFKCheck0 = connection.prepareStatement("SET FOREIGN_KEY_CHECKS = 0");
                queryFKCheck0.executeUpdate();
                queryFKCheck0.close();

                PreparedStatement queryMembers = connection.prepareStatement("DELETE FROM player WHERE teamId = ?");
                queryMembers.setInt(1, getTeamId(teamName));
                queryMembers.executeUpdate();
                queryMembers.close();

                PreparedStatement queryPermissions = connection.prepareStatement("DELETE FROM assignement WHERE teamId = ?");
                queryPermissions.setInt(1, getTeamId(teamName));
                queryPermissions.executeUpdate();
                queryPermissions.close();

                PreparedStatement queryAlliances = connection.prepareStatement("DELETE FROM alliance WHERE teamId = ? OR teamAllianceId = ?");
                queryAlliances.setInt(1, getTeamId(teamName));
                queryAlliances.setInt(2, getTeamId(teamName));
                queryAlliances.executeUpdate();
                queryAlliances.close();

                PreparedStatement queryTeamStorage = connection.prepareStatement("DELETE FROM teamStorage WHERE teamId = ?");
                queryTeamStorage.setInt(1, getTeamId(teamName));
                queryTeamStorage.executeUpdate();
                queryTeamStorage.close();

                PreparedStatement queryTeams = connection.prepareStatement("DELETE FROM team WHERE teamName = ?");
                queryTeams.setString(1, teamName);
                queryTeams.executeUpdate();
                queryTeams.close();

                PreparedStatement queryFKCheck1 = connection.prepareStatement("SET FOREIGN_KEY_CHECKS = 1");
                queryFKCheck1.executeUpdate();
                queryFKCheck1.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public void updateTeamDescription(String teamName, String newTeamDescription){
        if(isConnected()){
            try {
                PreparedStatement queryTeam = connection.prepareStatement("UPDATE team SET teamDescription = ? WHERE teamName = ?");
                queryTeam.setString(1, newTeamDescription);
                queryTeam.setString(2, teamName);
                queryTeam.executeUpdate();
                queryTeam.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateTeamOwner(String teamName, String teamOwner, String newTeamOwner){
        if(isConnected()){
            try {
                PreparedStatement queryOwner = connection.prepareStatement("UPDATE player SET teamRank = ? WHERE teamId = (SELECT teamId FROM team WHERE teamName = ?) AND playerName = ?");
                queryOwner.setInt(1, 1);
                queryOwner.setString(2, teamName);
                queryOwner.setString(3, teamOwner);
                queryOwner.executeUpdate();
                queryOwner.close();

                PreparedStatement queryNewOwner = connection.prepareStatement("UPDATE player SET teamRank = ? WHERE teamId = (SELECT teamId FROM team WHERE teamName = ?) AND playerName = ?");
                queryNewOwner.setInt(1, 0);
                queryNewOwner.setString(2, teamName);
                queryNewOwner.setString(3, newTeamOwner);
                queryNewOwner.executeUpdate();
                queryNewOwner.close();

                PreparedStatement queryTeam = connection.prepareStatement("UPDATE team SET teamOwnerPlayerId = (SELECT playerId FROM player WHERE playerName = ?) WHERE teamName = ?");
                queryTeam.setString(1, newTeamOwner);
                queryTeam.setString(2, teamName);
                queryTeam.executeUpdate();
                queryTeam.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    public void promoteMember(String playerName){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE player SET teamRank = teamRank - 1 WHERE playerName = ?");
                query.setString(1, playerName);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void demoteMember(String playerName){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE player SET teamRank = teamRank + 1 WHERE playerName = ?");
                query.setString(1, playerName);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    public void insertStorageId(int storageId){
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

    @Override
    public boolean storageIdExist(int storageId){
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

    @Override
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

    @Override
    public byte[] getStorageContent(String teamName, Integer storageId){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT storageContent FROM teamStorage AS ts INNER JOIN team AS t ON ts.teamId = t.teamId WHERE t.teamName = ? AND ts.storageId = ?");
                query.setString(1, teamName);
                query.setInt(2, storageId);
                ResultSet result = query.executeQuery();
                if(result.next()){
                    return result.getBytes("storageContent");
                }
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void insertStorageContent(String teamName, Integer storageId, byte[] storageContent){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("INSERT INTO teamStorage VALUES (?, ?, ?)");
                query.setInt(1, storageId);
                query.setInt(2, getTeamId(teamName));
                query.setBytes(3, storageContent);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateStorageContent(String teamName, Integer storageId, byte[] storageContent){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE teamStorage SET storageContent = ? WHERE teamId = ? AND storageId = ?");
                query.setBytes(1, storageContent);
                query.setInt(2, getTeamId(teamName));
                query.setInt(3, storageId);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void saveStorage(Storage storage){
        String teamName = storage.getTeam().getTeamName();
        int storageId = storage.getStorageId();
        byte[] storageContent = storage.getStorageContent();
        if (!teamHasStorage(teamName, storageId)){
            if (!storageIdExist(storageId)){
                insertStorageId(storageId);
            }
            insertStorageContent(teamName, storageId, storageContent);
        }else{
            updateStorageContent(teamName, storageId, storageContent);
        }
    }

    @Override
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

    @Override
    public void removeAlliance(String teamName, String allianceName){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("DELETE FROM alliance WHERE (teamId = (SELECT t.teamId FROM team t WHERE t.teamName = ?) AND teamAllianceId = (SELECT t.teamId FROM team t WHERE t.teamName = ?)) OR (teamId = (SELECT t.teamId FROM team t WHERE t.teamName = ?) AND teamAllianceId = (SELECT t.teamId FROM team t WHERE t.teamName = ?))");
                query.setString(1, teamName);
                query.setString(2, allianceName);
                query.setString(3, allianceName);
                query.setString(4, teamName);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
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

    @Override
    public ArrayList<String> getMembers(){
        if(isConnected()){
            try {
                ArrayList<String> members = new ArrayList<>();
                PreparedStatement query = connection.prepareStatement("SELECT playerName FROM player");
                ResultSet result = query.executeQuery();
                while(result.next()){
                    members.add(result.getString("playerName"));
                }
                query.close();
                return members;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public Team getTeamFromPlayerName(String playerName){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT t.teamUuid, t.teamName, t.teamPrefix, t.teamDescription, t.teamMoney, t.creationDate, t.improvLvlMembers, t.teamStorageLvl, t.unlockedTeamBank, o.playerName AS 'ownerName' FROM team AS t INNER JOIN player p ON t.teamId = p.teamId INNER JOIN player o ON t.teamOwnerPlayerId = o.playerId WHERE p.playerName = ?");
                query.setString(1, playerName);
                ResultSet result = query.executeQuery();
                if(result.next()){
                    return new Team(
                        UUID.fromString(result.getString("teamUuid")),
                        result.getString("teamName"),
                        result.getString("teamPrefix"),
                        result.getString("teamDescription"),
                        result.getInt("teamMoney"),
                        result.getString("creationDate"),
                        result.getInt("improvLvlMembers"),
                        result.getInt("teamStorageLvl"),
                        (1 == result.getInt("unlockedTeamBank")),
                        result.getString("ownerName")
                    );
                }
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public Team getTeamFromTeamName(String teamName){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT t.teamUuid, t.teamName, t.teamPrefix, t.teamDescription, t.teamMoney, t.creationDate, t.improvLvlMembers, t.teamStorageLvl, t.unlockedTeamBank, o.playerName AS 'ownerName' FROM team AS t INNER JOIN player o ON t.teamOwnerPlayerId = o.playerId WHERE t.teamName = ?");
                query.setString(1, teamName);
                ResultSet result = query.executeQuery();
                if(result.next()){
                    return new Team(
                        UUID.fromString(result.getString("teamUuid")),
                        result.getString("teamName"),
                        result.getString("teamPrefix"),
                        result.getString("teamDescription"),
                        result.getInt("teamMoney"),
                        result.getString("creationDate"),
                        result.getInt("improvLvlMembers"),
                        result.getInt("teamStorageLvl"),
                        (1 == result.getInt("unlockedTeamBank")),
                        result.getString("ownerName")
                    );
                }
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public ArrayList<Team> getTeams(){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT * FROM team INNER JOIN player ON team.teamOwnerPlayerId = player.playerId");
                ResultSet result = query.executeQuery();
                ArrayList<Team> teams = new ArrayList<>();
                while(result.next()){
                    teams.add(new Team(
                        UUID.fromString(result.getString("teamUuid")),
                        result.getString("teamName"),
                        result.getString("teamPrefix"),
                        result.getString("teamDescription"),
                        result.getInt("teamMoney"),
                        result.getString("creationDate"),
                        result.getInt("improvLvlMembers"),
                        result.getInt("teamStorageLvl"),
                        (1 == result.getInt("unlockedTeamBank")),
                        result.getString("playerName")
                    ));
                }
                query.close();
                return teams;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public ArrayList<Member> getTeamMembers(String teamName){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT p.playerName, p.teamRank, p.joinDate FROM player AS p INNER JOIN team AS t ON p.teamId = t.teamId WHERE t.teamName = ? ORDER BY p.teamRank ASC, p.playerName ASC;");
                query.setString(1, teamName);
                ResultSet result = query.executeQuery();
                ArrayList<Member> teamMembers = new ArrayList<>();
                while(result.next()){
                    teamMembers.add(
                        new Member(
                            result.getString("playerName"),
                            result.getInt("teamRank"),
                            result.getString("joinDate"),
                            StelyTeamPlugin.getPlugin().getSQLiteManager().getUUID(result.getString("playerName"))
                        )
                    );
                }
                query.close();
                return teamMembers;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public ArrayList<Permission> getTeamAssignement(String teamName){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT a.permLabel, a.teamRank FROM assignement AS a INNER JOIN team AS t ON a.teamId = t.teamId WHERE t.teamName = ?;");
                query.setString(1, teamName);
                ResultSet result = query.executeQuery();
                ArrayList<Permission> teamAssignement = new ArrayList<>();
                while(result.next()){
                    teamAssignement.add(
                        new Permission(
                            result.getString("permLabel"),
                            result.getInt("teamRank")
                        )
                    );
                }
                query.close();
                return teamAssignement;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public ArrayList<Alliance> getTeamAlliances(String teamName){
        if(isConnected()){
            try {
                ArrayList<Alliance> alliances = new ArrayList<>();
                PreparedStatement queryTeam = connection.prepareStatement("SELECT t.teamName, a.allianceDate FROM team AS t INNER JOIN alliance AS a ON t.teamId = a.teamAllianceId WHERE a.teamId = ?");
                queryTeam.setInt(1, getTeamId(teamName));
                ResultSet resultTeam = queryTeam.executeQuery();
                while(resultTeam.next()){
                    alliances.add(
                        new Alliance(
                            resultTeam.getString("teamName"),
                            resultTeam.getString("allianceDate")
                        )
                    );
                }
                queryTeam.close();
                
                PreparedStatement queryAlliance = connection.prepareStatement("SELECT t.teamName, a.allianceDate FROM team AS t INNER JOIN alliance AS a ON t.teamId = a.teamId WHERE a.teamAllianceId = ?");
                queryAlliance.setInt(1, getTeamId(teamName));
                ResultSet resultAlliance = queryAlliance.executeQuery();
                while(resultAlliance.next()){
                    alliances.add(
                        new Alliance(
                            resultAlliance.getString("teamName"),
                            resultAlliance.getString("allianceDate")
                        )
                    );
                }
                queryAlliance.close();
                return alliances;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public Map<Integer, Storage> getTeamStorages(Team team){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT ts.storageId, ts.storageContent FROM teamStorage AS ts INNER JOIN team AS t ON ts.teamId = t.teamId WHERE t.teamName = ?;");
                query.setString(1, team.getTeamName());
                ResultSet result = query.executeQuery();
                Map<Integer, Storage> teamStorage = new HashMap<>();
                while(result.next()){
                    teamStorage.put(
                        result.getInt("storageId"),
                        new Storage(
                            team,
                            result.getInt("storageId"),
                            null,
                            result.getBytes("storageContent")
                        )
                    );
                }
                query.close();
                return teamStorage;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public int getPlayerId(String playerName){
        if (isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT playerId FROM player WHERE playerName = ?");
                query.setString(1, playerName);
                ResultSet result = query.executeQuery();
                int playerId = 0;
                if(result.next()){
                    playerId = result.getInt("playerId");
                }
                query.close();
                return playerId;
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public int getTeamId(String teamName){
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

    @Override
    public Team getTeamFromTeamUuid(UUID teamUuid) {
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT t.teamUuid, t.teamName, t.teamPrefix, t.teamDescription, t.teamMoney, t.creationDate, t.improvLvlMembers, t.teamStorageLvl, t.unlockedTeamBank, o.playerName AS 'ownerName' FROM team AS t INNER JOIN player o ON t.teamOwnerPlayerId = o.playerId WHERE t.teamUuid = ?");
                query.setString(1, teamUuid.toString());
                ResultSet result = query.executeQuery();
                if(result.next()){
                    return new Team(
                        UUID.fromString(result.getString("teamUuid")),
                        result.getString("teamName"),
                        result.getString("teamPrefix"),
                        result.getString("teamDescription"),
                        result.getInt("teamMoney"),
                        result.getString("creationDate"),
                        result.getInt("improvLvlMembers"),
                        result.getInt("teamStorageLvl"),
                        (1 == result.getInt("unlockedTeamBank")),
                        result.getString("ownerName")
                    );
                }
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    
    @Override
    public String getCurrentDate(){
        return new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
    }
}