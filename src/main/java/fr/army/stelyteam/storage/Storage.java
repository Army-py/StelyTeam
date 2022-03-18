package fr.army.stelyteam.storage;

import fr.army.stelyteam.team.Team;

import java.util.UUID;

public interface Storage {

    Team loadTeam(UUID teamID);

    void saveTeam(Team team);

    UUID getPlayerTeam(UUID playerId);

    void savePlayerTeams(PlayerTeamTracker playerTeamTracker);

}
