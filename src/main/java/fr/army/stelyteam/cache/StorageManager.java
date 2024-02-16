package fr.army.stelyteam.cache;

import fr.army.stelyteam.entity.impl.TeamEntity;
import fr.army.stelyteam.repository.callback.AsyncCallBackObject;
import fr.army.stelyteam.repository.impl.TeamRepository;
import fr.army.stelyteam.team.Team;
import jakarta.persistence.EntityManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class StorageManager {

    // private final DatabaseManager databaseManager = StelyTeamPlugin.getPlugin().getDatabaseManager();
    // private final Storage storage;

    private final TeamRepository teamRepository;


    public StorageManager(EntityManager entityManager){
        this.teamRepository = new TeamRepository(TeamEntity.class, entityManager);
    }


    @Nullable
    public TeamEntity retrieveTeam(@NotNull String teamName) {
        return teamRepository.findByTeamName(teamName);
    }

    @Nullable
    public TeamEntity retrievePlayerTeam(@NotNull String playerName) {
        return teamRepository.findByPlayerName(playerName);
    }

    public TeamEntity retrieveTeam(@NotNull UUID teamId) {
        return teamRepository.findByTeamUuid(teamId);
    }

    public void saveTeam(@NotNull Team team) {
        TeamEntity teamEntity = TeamEntity.Builder.fromTeam(team, teamRepository);
        if (teamRepository.findByTeamUuid(team.getId()) == null) {
            teamRepository.insert(teamEntity);
        } else {
            teamRepository.update(teamEntity, result -> {});
        }
    }

    public <T> void retrieve(UUID teamId, @NotNull SetProperty<?, ?>... properties) {
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
