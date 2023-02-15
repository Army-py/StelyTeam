package fr.army.stelyteam.utils.manager.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.Alliance;
import fr.army.stelyteam.utils.Member;
import fr.army.stelyteam.utils.Permission;
import fr.army.stelyteam.utils.Storage;
import fr.army.stelyteam.utils.Team;

public abstract class DatabaseManager {

    protected StelyTeamPlugin plugin;

    public DatabaseManager(StelyTeamPlugin plugin){
        this.plugin = plugin;
    }

    
    public static DatabaseManager connect(StelyTeamPlugin plugin) throws ClassNotFoundException, SQLException {
        YamlConfiguration config = plugin.getConfig();
        DatabaseManager sqlManager;
        switch (config.getString("database_type")) {
            case "mysql":
                sqlManager = new MySQLManager(plugin);
                break;
            default:
                sqlManager = new SQLiteManager(plugin);
                break;
        }
        sqlManager.init();
        return sqlManager;
    }

    public abstract void init() throws ClassNotFoundException, SQLException;
    
    public abstract boolean isConnected();

    public abstract void disconnect();

    public abstract Connection getConnection();

    public abstract void createTables();

    public abstract boolean isOwner(String playerName);

    public abstract boolean isMember(String playerName);

    public abstract boolean teamNameExists(String teamName);

    public abstract void insertTeam(String teamName, String teamPrefix, String ownerName);

    public abstract void insertMember(String playerName, String teamName);

    public abstract void removeTeam(String teamName);

    public abstract void removeMember(String playerName, String teamName);

    public abstract void updateTeamName(String teamName, String newTeamName);

    public abstract void updateTeamPrefix(String teamName, String newTeamPrefix);

    public abstract void updateTeamDescription(String teamName, String newTeamDescription);

    public abstract void updateTeamOwner(String teamName, String teamOwner, String newTeamOwner);

    public abstract void updateUnlockedTeamBank(String teamName);

    public abstract void incrementImprovLvlMembers(String teamName);

    public abstract void incrementTeamStorageLvl(String teamName);

    public abstract void incrementTeamMoney(String teamName, double money);

    public abstract void decrementImprovLvlMembers(String teamName);

    public abstract void decrementTeamMoney(String teamName, double money);

    public abstract void promoteMember(String playerName);

    public abstract void demoteMember(String playerName);

    public abstract String getTeamNameFromPlayerName(String playerName);

    public abstract Double getTeamMoney(String teamName);

    public abstract ArrayList<String> getTeamMembersWithRank(String teamName, int rank);

    public abstract ArrayList<String> getTeamsName();

    public abstract void insertAssignement(String teamName, String permLabel, Integer teamRank);

    public abstract void incrementAssignement(String teamName, String permLabel);

    public abstract void decrementAssignement(String teamName, String permLabel);

    public abstract void insertStorageId(int storageId);

    public abstract boolean storageIdExist(int storageId);

    public abstract boolean teamHasStorage(String teamName, Integer storageId);

    public abstract byte[] getStorageContent(String teamName, Integer storageId);

    public abstract void insertStorageContent(String teamName, Integer storageId, byte[] storageContent);

    public abstract void updateStorageContent(String teamName, Integer storageId, byte[] storageContent);

    public abstract void saveStorage(Storage storage);

    public abstract void insertAlliance(String teamName, String allianceName);

    public abstract void removeAlliance(String teamName, String allianceName);

    public abstract boolean isAlliance(String teamName, String allianceName);

    public abstract ArrayList<String> getMembers();

    public abstract Team getTeamFromPlayerName(String playerName);

    public abstract Team getTeamFromTeamName(String teamName);

    public abstract ArrayList<Team> getTeams();

    public abstract ArrayList<Member> getTeamMembers(String teamName);

    public abstract ArrayList<Permission> getTeamAssignement(String teamName);

    public abstract ArrayList<Alliance> getTeamAlliances(String teamName);

    public abstract Map<Integer, Storage> getTeamStorages(Team team);

    public abstract int getPlayerId(String playerName);

    public abstract int getTeamId(String teamName);

    public abstract String getCurrentDate();
}
