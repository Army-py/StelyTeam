package fr.army.stelyteam.storage.database;

import fr.army.stelyteam.storage.PlayerTeamTracker;
import fr.army.stelyteam.storage.Storage;
import fr.army.stelyteam.team.Team;

import java.util.UUID;

public class DataBaseStorage implements Storage {

    //TODO Implement this class with the current database system

    @Override
    public Team loadTeam(UUID teamID) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void saveTeam(Team team) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public UUID getPlayerTeam(UUID playerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void savePlayerTeams(PlayerTeamTracker playerTeamTracker) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
