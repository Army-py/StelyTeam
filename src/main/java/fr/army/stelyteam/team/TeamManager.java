package fr.army.stelyteam.team;

import fr.army.stelyteam.cache.SaveValue;
import fr.army.stelyteam.cache.StorageManager;
import fr.army.stelyteam.cache.TeamCache;
import fr.army.stelyteam.cache.TeamField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class TeamManager {

    private final StorageManager storageManager;
    private final TeamCache teamCache;

    public TeamManager(StorageManager storageManager, TeamCache teamCache) {
        this.storageManager = storageManager;
        this.teamCache = teamCache;
    }

    @Nullable
    public Team getTeam(@NotNull UUID teamId, @NotNull TeamField... fields) {
        final Team cachedTeam = teamCache.getTeam(teamId);
        final List<TeamField> needLoadFields = cachedTeam == null ? Arrays.asList(fields) : cachedTeam.getNeedLoad(fields);
        if (cachedTeam != null && needLoadFields.isEmpty()) {
            return cachedTeam;
        }
        final TeamSnapshot storedTeam = storageManager.retrieveTeam(teamId, fields);
        if (storedTeam == null) {
            return null;
        }
        final Team team = cachedTeam == null ? new Team(storedTeam.uuid()) : cachedTeam;
        team.loadUnsafe(storedTeam);
        return team;
    }

    @Nullable
    public Team getTeam(@NotNull String teamName, @NotNull TeamField... fields) {
        // TODO First check from cache then load from storage if needed (need improvement in TeamCache)
        // Then the implementation of this method should be exactly like #getTeam(UUID, TeamField[])
        final TeamSnapshot storedTeam = storageManager.retrieveTeam(teamName, fields);
        if (storedTeam == null) {
            return null;
        }
        final Team cachedTeam = teamCache.getTeam(storedTeam.uuid());
        final Team team = cachedTeam == null ? new Team(storedTeam.uuid()) : cachedTeam;
        team.loadUnsafe(storedTeam);
        return team;
    }

    public void saveTeam(@NotNull Team team) {
        final List<SaveValue<?>> saveValues = new LinkedList<>();
        team.save(saveValues);
        if (saveValues.isEmpty()) {
            return;
        }
        // TODO Implement saving
    }

}
