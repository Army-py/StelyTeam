package fr.army.stelyteam.cache;

import fr.army.stelyteam.team.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StorageManager {

    @Nullable
    public Team retreiveTeam(@NotNull String teamName, TeamField... fields) {
        // TODO Fetch the team from the current storage
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Nullable
    public Team retreivePlayerTeam(@NotNull String playerName, TeamField... fields) {
        // TODO Fetch the team from the current storage
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
