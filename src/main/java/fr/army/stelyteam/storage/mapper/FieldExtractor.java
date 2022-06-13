package fr.army.stelyteam.storage.mapper;

import fr.army.stelyteam.team.Team;

public interface FieldExtractor {

    /**
     * Extract the value from a {@link Team}
     *
     * @param team The {@link Team} that contains the value
     * @return The extracted value
     */
    Object extract(Team team);

}
