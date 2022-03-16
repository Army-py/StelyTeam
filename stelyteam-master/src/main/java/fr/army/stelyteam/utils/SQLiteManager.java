package fr.army.stelyteam.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;

public class SQLiteManager {
    private String database;
    private Connection connection;

    public SQLiteManager() {
        this.database = "data.db";
    }


    public boolean isConnected() {
        return connection == null ? false : true;
    }


    public void connect() throws ClassNotFoundException, SQLException{
        if(!isConnected()){
            this.connection = DriverManager.getConnection("jdbc:sqlite:"+ StelyTeamPlugin.instance.getDataFolder().getAbsolutePath()+"/"+this.database);
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
                PreparedStatement queryHomes = connection.prepareStatement("CREATE TABLE IF NOT EXISTS 'homes' ('id' INTEGER, 'team_id' TEXT, world TEXT, 'x' REAL, 'y' REAL, 'z' REAL, 'yaw' REAL, PRIMARY KEY('id' AUTOINCREMENT));");
                queryHomes.executeUpdate();
                queryHomes.close();

                PreparedStatement queryPlayers = connection.prepareStatement("CREATE TABLE IF NOT EXISTS 'players' ('uuid' TEXT, 'playername' TEXT, PRIMARY KEY('uuid'));");
                queryPlayers.executeUpdate();
                queryPlayers.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public boolean isSet(String team_id){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT team_id FROM homes WHERE team_id = ?");
                query.setString(1, team_id);
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


    public void addHome(String team_id, String world, double x, double y, double z, double yaw){
        if(isConnected()){
            try {
                PreparedStatement queryPlayers = connection.prepareStatement("INSERT INTO homes VALUES (null, ?, ?, ?, ?, ?, ?)");
                queryPlayers.setString(1, team_id);
                queryPlayers.setString(2, world);
                queryPlayers.setDouble(3, x);
                queryPlayers.setDouble(4, y);
                queryPlayers.setDouble(5, z);
                queryPlayers.setDouble(6, yaw);
                queryPlayers.executeUpdate();
                queryPlayers.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void updateHome(String team_id, String world, double x, double y, double z, double yaw){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("UPDATE homes SET world = ?, x = ?, y = ?, z = ?, yaw = ? WHERE team_id = ?");
                query.setString(1, world);
                query.setDouble(2, x);
                query.setDouble(3, y);
                query.setDouble(4, z);
                query.setDouble(5, yaw);
                query.setString(6, team_id);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void removeHome(String team_id){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("DELETE FROM homes WHERE team_id = ?");
                query.setString(1, team_id);
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public String getWorld(String team_id){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT world FROM homes WHERE team_id = ?");
                query.setString(1, team_id);
                ResultSet result = query.executeQuery();
                boolean isParticipant = result.next();
                if(isParticipant){
                    String world = result.getString("world");
                    query.close();
                    return world;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public double getX(String team_id){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT x FROM homes WHERE team_id = ?");
                query.setString(1, team_id);
                ResultSet result = query.executeQuery();
                boolean isParticipant = result.next();
                if(isParticipant){
                    double x = result.getDouble("x");
                    query.close();
                    return x;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }


    public double getY(String team_id){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT y FROM homes WHERE team_id = ?");
                query.setString(1, team_id);
                ResultSet result = query.executeQuery();
                boolean isParticipant = result.next();
                if(isParticipant){
                    double y = result.getDouble("y");
                    query.close();
                    return y;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }


    public double getZ(String team_id){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT z FROM homes WHERE team_id = ?");
                query.setString(1, team_id);
                ResultSet result = query.executeQuery();
                boolean isParticipant = result.next();
                if(isParticipant){
                    double z = result.getDouble("z");
                    query.close();
                    return z;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }


    public double getYaw(String team_id){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT yaw FROM homes WHERE team_id = ?");
                query.setString(1, team_id);
                ResultSet result = query.executeQuery();
                boolean isParticipant = result.next();
                if(isParticipant){
                    double yaw = result.getDouble("yaw");
                    query.close();
                    return yaw;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }


    public void registerPlayer(Player player){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("INSERT INTO players VALUES (?, ?)");
                query.setString(1, player.getUniqueId().toString());
                query.setString(2, player.getName());
                query.executeUpdate();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public boolean isRegistered(String playername){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT playername FROM players WHERE playername = ?");
                query.setString(1, playername);
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


    public UUID getUUID(String playername){
        if(isConnected()){
            try {
                PreparedStatement query = connection.prepareStatement("SELECT uuid FROM players WHERE playername = ?");
                query.setString(1, playername);
                ResultSet result = query.executeQuery();
                boolean isParticipant = result.next();
                UUID uuid = null;
                if(isParticipant){
                    uuid = UUID.fromString(result.getString("uuid"));
                }
                query.close();
                return uuid;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}