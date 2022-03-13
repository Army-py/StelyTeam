package fr.army.stelyteam.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import fr.army.stelyteam.App;

public class SQLManager {
    
    private String host;
    private String database;
    private String user;
    private String password;
    private int port;

    private Connection connection;

    public SQLManager() {
        // this.host = App.config.getString("sql.host");
        // this.database = App.config.getString("sql.database");
        // this.user = App.config.getString("sql.user");
        // this.password = App.config.getString("sql.password");
        // this.port = App.config.getInt("sql.port");

        this.database = "bungeecord_StelyTeam.db";
    }


    public boolean isConnected() {
        return connection == null ? false : true;
    }


    public void connect() throws ClassNotFoundException, SQLException{
        if(!isConnected()){
            this.connection = DriverManager.getConnection("jdbc:sqlite:"+App.instance.getDataFolder().getAbsolutePath()+"/"+this.database);
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


    public boolean isOwner(String playername){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT owner FROM teams WHERE owner = ?");
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
                PreparedStatement query = connection.prepareStatement("SELECT ID_pseudo FROM pseudos WHERE ID_pseudo = ? AND confiance = ?");
                query.setString(1, playername);
                query.setInt(2, 0);
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


    public boolean isMemberInTeam(String playername, String team_id){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT ID_pseudo FROM pseudos WHERE ID_pseudo = ? AND ID_team = ?");
                query.setString(1, playername);
                query.setString(2, team_id);
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


    public boolean isAdmin(String playername){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT ID_pseudo FROM pseudos WHERE ID_pseudo = ? AND confiance = ?");
                query.setString(1, playername);
                query.setInt(2, 1);
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


    public boolean hasUnlockedTeamBank(String teamID){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT ID_team FROM teams WHERE ID_team = ? AND teamcompte = 1");
                query.setString(1, teamID);
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


    public boolean teamIdExist(String teamID){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT ID_team FROM teams WHERE ID_team = ?");
                query.setString(1, teamID);
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
                PreparedStatement query = connection.prepareStatement("SELECT prefixteam FROM teams WHERE prefixteam = ?");
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
                PreparedStatement queryTeam = connection.prepareStatement("INSERT INTO teams (ID_team, prefixteam, owner, money, datecreation, niveaux, teamcompte) VALUES (?, ?, ?, ?, ?, ?, ?)");
                queryTeam.setString(1, teamID);
                queryTeam.setString(2, teamPrefix);
                queryTeam.setString(3, owner);
                queryTeam.setInt(4, 0);
                queryTeam.setString(5, new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime()));
                queryTeam.setInt(6, 0);
                queryTeam.setInt(7, 0);
                queryTeam.executeUpdate();
                queryTeam.close();

                PreparedStatement queryMember = connection.prepareStatement("INSERT INTO pseudos (ID_pseudo, confiance, ID_team) VALUES (?, ?, ?)");
                queryMember.setString(1, owner);
                queryMember.setInt(2, 2);
                queryMember.setString(3, teamID);
                queryMember.executeUpdate();
                queryMember.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void insertMember(String playername, String teamID){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("INSERT INTO pseudos (ID_pseudo, confiance, ID_team) VALUES (?, ?, ?)");
                query.setString(1, playername);
                query.setInt(2, 0);
                query.setString(3, teamID);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void removeTeam(String teamID, String owner){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("DELETE FROM teams WHERE ID_team = ? AND owner = ?");
                query.setString(1, teamID);
                query.setString(2, owner);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void removeMember(String playername, String teamID){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("DELETE FROM pseudos WHERE ID_pseudo = ? AND ID_team = ?");
                query.setString(1, playername);
                query.setString(2, teamID);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void updateTeamID(String teamID, String newTeamID, String owner){
        if(isConnected()){
            try {
                PreparedStatement queryTeam = connection.prepareStatement("UPDATE teams SET ID_team = ? WHERE ID_team = ? AND owner = ?");
                queryTeam.setString(1, teamID);
                queryTeam.setString(2, newTeamID);
                queryTeam.setString(3, owner);
                queryTeam.executeUpdate();
                queryTeam.close();

                PreparedStatement queryMember = connection.prepareStatement("UPDATE pseudos SET ID_team = ? WHERE ID_team = ?");
                queryMember.setString(1, teamID);
                queryMember.setString(2, newTeamID);
                queryMember.executeUpdate();
                queryMember.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void updateTeamPrefix(String teamID, String newTeamPrefix, String owner){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE teams SET prefixteam = ? WHERE ID_team = ? AND owner = ?");
                query.setString(1, newTeamPrefix);
                query.setString(2, teamID);
                query.setString(3, owner);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void updateTeamOwner(String teamID, String newOwner, String owner){
        if(isConnected()){
            try {
                PreparedStatement queryMember = connection.prepareStatement("UPDATE pseudos SET confiance = ? WHERE ID_team = ? AND ID_pseudo = ?");
                queryMember.setInt(1, 1);
                queryMember.setString(2, teamID);
                queryMember.setString(3, owner);
                queryMember.executeUpdate();
                queryMember.close();

                PreparedStatement queryTeam = connection.prepareStatement("UPDATE teams SET owner = ? WHERE ID_team = ? AND owner = ?");
                queryTeam.setString(1, newOwner);
                queryTeam.setString(2, teamID);
                queryTeam.setString(3, owner);
                queryTeam.executeUpdate();
                queryTeam.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void updateUnlockTeamBank(String teamID){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE teams SET teamcompte = ? WHERE ID_team = ?");
                query.setInt(1, 1);
                query.setString(2, teamID);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void incrementTeamLevel(String teamID){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE teams SET niveaux = niveaux + 1 WHERE ID_team = ?");
                query.setString(1, teamID);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void setTeamAdmin(String teamID, String playername){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE pseudos SET confiance = ? WHERE ID_team = ?");
                query.setString(1, teamID);
                query.setInt(2, 1);
                query.setString(3, teamID);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void removeTeamAdmin(String teamID, String playername){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE pseudos SET confiance = ? WHERE ID_team = ?");
                query.setString(1, teamID);
                query.setInt(2, 0);
                query.setString(3, teamID);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public String getTeamIDFromOwner(String owner){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT ID_team FROM teams WHERE owner = ?");
                query.setString(1, owner);
                ResultSet result = query.executeQuery();
                if(result.next()){
                    return result.getString("ID_team");
                }
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public String getTeamIDFromPlayer(String playername){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT ID_team FROM pseudos WHERE ID_pseudo = ?");
                query.setString(1, playername);
                ResultSet result = query.executeQuery();
                if(result.next()){
                    return result.getString("ID_team");
                }
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public Integer getTeamLevel(String teamID){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT niveaux FROM teams WHERE ID_team = ?");
                query.setString(1, teamID);
                ResultSet result = query.executeQuery();
                if(result.next()){
                    return result.getInt("niveaux");
                }
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public ArrayList<String> getMembers(String teamID){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT ID_pseudo FROM pseudos WHERE ID_team = ?");
                query.setString(1, teamID);

                ResultSet result = query.executeQuery();
                ArrayList<String> data = new ArrayList<String>();
                while(result.next()){
                    data.add(result.getString("ID_pseudo"));
                }
                query.close();
                return data;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}