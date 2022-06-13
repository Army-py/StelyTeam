package fr.army.stelyteam.storage.mapper;

import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.team.TeamBuilder;

import java.util.*;

public class PlayerMapper implements FieldMapper {

    @Override
    public Object extract(Team team) {
        final Map<Integer, List<UUID>> uuids = new HashMap<>();
        for (var entry : team.getPlayers().getUuidRanks().entrySet()) {
            var rankedPlayers = uuids.computeIfAbsent(entry.getValue(), k -> new LinkedList<>());
            rankedPlayers.add(entry.getKey());
        }

        final int[] ranks = new int[uuids.size()];
        final UUID[][] uuidsArray = new UUID[uuids.size()][];
        final var itr = uuids.entrySet().iterator();
        int index = 0;
        for (var entry = itr.next(); itr.hasNext(); entry = itr.next(), index++) {
            ranks[index] = entry.getKey();
            uuidsArray[index] = entry.getValue().toArray(new UUID[0]);
        }

        return new RankedUUIDs(ranks, uuidsArray);
    }

    @Override
    public void inflate(Object value, TeamBuilder builder) {
        final RankedUUIDs rankedUUIDs = (RankedUUIDs) value;
        final UUID[][] storedPlayers = rankedUUIDs.uuids();
        if (storedPlayers.length == 0) {
            return;
        }
        final int[] ranks = rankedUUIDs.ranks();
        final Map<UUID, Integer> players = new HashMap<>();
        // Fill the map
        for (int index = 0; index < storedPlayers.length; index++) {
            final int rank = ranks[index];
            for (UUID uuid : storedPlayers[index]) {
                players.put(uuid, rank);
            }
        }

        builder.setPlayers(players);
    }

    public record RankedUUIDs(int[] ranks, UUID[][] uuids) {
    }

}
