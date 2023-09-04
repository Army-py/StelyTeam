package fr.army.stelyteam.utils.manager.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.team.Alliance;
import fr.army.stelyteam.team.Member;
import fr.army.stelyteam.team.Permission;
import fr.army.stelyteam.team.Storage;
import fr.army.stelyteam.team.Team;

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

    public abstract void insertTeam(Team team);

    public abstract void insertMember(String playerName, UUID teamUuid);

    public abstract void removeTeam(UUID teamUuid);

    public abstract void removeMember(String playerName, UUID teamUuid);

    public abstract void updateTeamName(UUID teamUuid, String newTeamName);

    public abstract void updateTeamPrefix(UUID teamUuid, String newTeamPrefix);

    public abstract void updateTeamDescription(UUID teamUuid, String newTeamDescription);

    public abstract void updateTeamOwner(UUID teamUuid, String teamOwner, String newTeamOwner);

    public abstract void updateUnlockedTeamBank(UUID teamUuid);

    public abstract void updateUnlockedTeamClaim(UUID teamUuid);

    public abstract void incrementImprovLvlMembers(UUID teamUuid);

    public abstract void incrementTeamStorageLvl(UUID teamUuid);

    public abstract void incrementTeamMoney(UUID teamUuid, double money);

    public abstract void decrementImprovLvlMembers(UUID teamUuid);

    public abstract void decrementTeamMoney(UUID teamUuid, double money);

    public abstract void promoteMember(String playerName);

    public abstract void demoteMember(String playerName);

    public abstract String getTeamNameFromPlayerName(String playerName);

    public abstract double getTeamMoney(UUID teamUuid);

    public abstract Set<String> getTeamMembersWithRank(UUID teamUuid, int rank);

    public abstract Set<String> getTeamsName();

    public abstract void insertAssignement(UUID teamUuid, String permLabel, Integer teamRank);

    public abstract void incrementAssignement(UUID teamUuid, String permLabel);

    public abstract void decrementAssignement(UUID teamUuid, String permLabel);

    public abstract void insertStorageId(int storageId);

    public abstract boolean storageIdExist(int storageId);

    public abstract boolean teamHasStorage(UUID teamUuid, Integer storageId);

    public abstract byte[] getStorageContent(UUID teamUuid, Integer storageId);

    public abstract void insertStorageContent(UUID teamUuid, Integer storageId, byte[] storageContent, String openedServer);

    public abstract void updateStorageContent(UUID teamUuid, Integer storageId, byte[] storageContent, String openedServer);

    public abstract void saveStorage(Storage storage);

    public abstract void insertAlliance(UUID teamUuid, UUID allianceUuid);

    public abstract void removeAlliance(UUID teamUuid, UUID allianceUuid);

    public abstract boolean isAlliance(UUID teamUuid, UUID allianceUuid);

    public abstract Set<String> getMembers();

    public abstract Team getTeamFromTeamUuid(UUID teamUuid);

    public abstract Team getTeamFromPlayerName(String playerName);

    public abstract Team getTeamFromTeamName(String teamName);

    public abstract Set<Team> getTeams();

    public abstract List<Member> getTeamMembers(UUID teamUuid);

    public abstract Set<Permission> getTeamAssignement(UUID teamUuid);

    public abstract List<Alliance> getTeamAlliances(UUID teamUuid);

    public abstract Map<Integer, Storage> getTeamStorages(UUID teamUuid);

    public abstract int getPlayerId(String playerName);

    public abstract int getTeamId(UUID teamUuid);

    public abstract String getCurrentDate();



    public abstract void registerPlayer(Player player);

    public abstract void registerPlayer(OfflinePlayer player);

    public abstract boolean isRegistered(String playerName);

    public abstract UUID getUUID(String playerName);

    public abstract String getPlayerName(UUID uuid);
}
