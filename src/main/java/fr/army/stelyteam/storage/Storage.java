package fr.army.stelyteam.storage;

import fr.army.stelyteam.team.Team;

import java.util.UUID;

public interface Storage {

    Team loadTeam(UUID teamID);

    Team saveTeam(Team team);

}
