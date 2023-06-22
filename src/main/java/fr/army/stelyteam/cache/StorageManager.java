package fr.army.stelyteam.cache;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.team.TeamSnapshot;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;
import fr.army.stelyteam.utils.manager.database.builder.PreparedSQLRequest;
import fr.army.stelyteam.utils.manager.database.builder.SQLResult;
import fr.army.stelyteam.utils.manager.database.builder.impl.fundamental.SelectQuery;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.UUID;

public class StorageManager {

    private final DatabaseManager databaseManager = StelyTeamPlugin.getPlugin().getDatabaseManager();
    private final Storage storage;

    @Nullable
    public TeamSnapshot retrieveTeam(@NotNull String teamName, @NotNull SaveField... fields) {
        String[] tables = new String[]{"team", "player"};
        SaveField[] columns = new SaveField[]{
            SaveField.NAME, 
            SaveField.PREFIX, 
            SaveField.DESCRIPTION, 
            SaveField.CREATION_DATE,
            // SaveField.UPGRADES_MEMBERS,
            SaveField.BANK_BALANCE,
            SaveField.BANK_UNLOCKED,
            SaveField.TEAM_UUID,
        };
        String[] conditions = new String[]{
            "team.teamid = member.teamId", 
            "player.teamRank = 0", 
            "team.teamName = " + teamName
        };

        SQLResult result = databaseManager.get(tables, columns, conditions, null);

        try {
            return result.getTeamSnapshot();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public TeamSnapshot retrievePlayerTeam(@NotNull String playerName, @NotNull SaveField... fields) {
        String[] tables = new String[]{"team t", "player o", "player p"};
        SaveField[] columns = new SaveField[]{
            SaveField.NAME, 
            SaveField.PREFIX, 
            SaveField.DESCRIPTION, 
            SaveField.CREATION_DATE,
            // SaveField.UPGRADES_MEMBERS,
            SaveField.BANK_BALANCE,
            SaveField.BANK_UNLOCKED,
            SaveField.TEAM_UUID,
        };
        String[] conditions = new String[]{
            "(t.teamid = o.teamId AND t.teamId = p.teamId) ", 
            "o.teamRank = 0", 
            "p.playerName = " + playerName
        };

        SQLResult result = databaseManager.get(tables, columns, conditions, null);

        try {
            return result.getTeamSnapshot();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public TeamSnapshot retrieveTeam(@NotNull UUID teamId, @NotNull SaveField... fields) {
        String[] tables = new String[]{"team", "player"};
        SaveField[] columns = new SaveField[]{
            SaveField.NAME, 
            SaveField.PREFIX, 
            SaveField.DESCRIPTION, 
            SaveField.CREATION_DATE,
            // SaveField.UPGRADES_MEMBERS,
            SaveField.BANK_BALANCE,
            SaveField.BANK_UNLOCKED,
            SaveField.TEAM_UUID,
        };
        String[] conditions = new String[]{
            "team.teamid = player.teamId", 
            "player.teamRank = 0", 
            "team.teamUuid = " + teamId.toString()
        };

        SQLResult result = databaseManager.get(tables, columns, conditions, null);

        try {
            return result.getTeamSnapshot();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> void retrieve(UUID teamId, @NotNull SetProperty<?>... properties) {
        // TODO Fetch team informations base on the specified properties from the current storage
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void save(@NotNull UUID teamId, @NotNull Property<?>... properties) {
        // TODO Save team properties
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void remove(UUID teamId) {
        // TODO Remove the team from the database
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
