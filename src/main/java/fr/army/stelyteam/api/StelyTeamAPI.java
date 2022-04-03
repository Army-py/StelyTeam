package fr.army.stelyteam.api;

import fr.army.stelyteam.storage.TeamManager;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class StelyTeamAPI {

    private final TeamManager teamManager;

    public StelyTeamAPI(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    public Optional<? extends ITeam> getPlayerTeam(UUID player) {
        return teamManager.getLoadedPlayerTeam(player);
    }

    public CompletableFuture<? extends Optional<? extends ITeam>> getOfflinePlayerTeam(UUID teamId) {
        return teamManager.getOrLoadPlayerTeam(teamId);
    }

}
