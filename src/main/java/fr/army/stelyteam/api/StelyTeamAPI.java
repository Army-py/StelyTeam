package fr.army.stelyteam.api;

import fr.army.stelyteam.storage.TeamManager;

import java.util.UUID;

public class StelyTeamAPI {

    private final TeamManager teamManager;

    public StelyTeamAPI(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    public ITeam getPlayerTeam(UUID player) {
        return teamManager.getPlayerTeam(player);
    }

    public ITeam getOfflinePlayerTeam(UUID teamId) {
        return teamManager.getOrLoadTeam(teamId);
    }

}
