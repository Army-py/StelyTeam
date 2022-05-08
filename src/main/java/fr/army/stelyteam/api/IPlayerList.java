package fr.army.stelyteam.api;

import java.util.Set;
import java.util.UUID;

public interface IPlayerList {

    Set<UUID> getIds();

    Set<UUID> getIds(int rank);

}
