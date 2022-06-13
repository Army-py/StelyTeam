package fr.army.stelyteam.storage.mapper;

import fr.army.stelyteam.team.Team;

public interface FieldInflater {

    /**
     * Inflate a value in a {@link Team}
     *
     * @param value The value to inflate
     * @param team  The targeted {@link Team}
     */
    void inflate(Object value, Team team);

}
