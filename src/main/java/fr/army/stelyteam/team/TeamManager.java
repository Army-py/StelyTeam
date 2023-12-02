package fr.army.stelyteam.team;

import fr.army.stelyteam.cache.SaveField;
import fr.army.stelyteam.cache.SaveProperty;
import fr.army.stelyteam.cache.StorageManager;
import fr.army.stelyteam.cache.TeamCache;
import fr.army.stelyteam.entity.impl.TeamEntity;
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
    public Team getTeam(@NotNull UUID teamId, @NotNull SaveField... fields) {
        final Team cachedTeam = teamCache.getTeam(teamId);
        final List<SaveField> needLoadFields = cachedTeam == null ? Arrays.asList(fields) : cachedTeam.getNeedLoad(fields);
        if (cachedTeam != null && needLoadFields.isEmpty()) {
            return cachedTeam;
        }
        final TeamEntity storedTeam = storageManager.retrieveTeam(teamId);
        if (storedTeam == null) {
            return null;
        }
        return cachedTeam == null ? new Team(storedTeam.getUuid()).loadUnsafe(storedTeam) : cachedTeam;
    }

    @Nullable
    public Team getTeam(@NotNull String teamName, @NotNull SaveField... fields) {
        final Team cachedTeam = teamCache.getTeam(teamName);
        final List<SaveField> needLoadFields = cachedTeam == null ? Arrays.asList(fields) : cachedTeam.getNeedLoad(fields);
        if (cachedTeam != null && needLoadFields.isEmpty()) {
            return cachedTeam;
        }
        final TeamEntity storedTeam = storageManager.retrieveTeam(teamName);
        if (storedTeam == null) {
            return null;
        }
        return cachedTeam == null ? new Team(storedTeam.getUuid()).loadUnsafe(storedTeam) : cachedTeam;
    }

    public void saveTeam(@NotNull Team team) {
        final List<SaveProperty<?>> saveValues = new LinkedList<>();
        team.save(saveValues);
        if (saveValues.isEmpty()) {
            return;
        }
        // TODO Implement saving
    }

    public void removeTeam(UUID teamId) {
    }
}
