package fr.army.stelyteam.storage.mapper;

import fr.army.stelyteam.team.TeamBuilder;

public interface FieldInflater {

    /**
     * Inflate a value in a {@link TeamBuilder}
     *
     * @param value   The value to inflate
     * @param builder The {@link TeamBuilder}
     */
    void inflate(Object value, TeamBuilder builder);

}
