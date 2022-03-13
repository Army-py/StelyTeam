package fr.army.stelyteam.api;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface ITeam {

    String getPrefix();

    String getSuffix();

    UUID getCreator();

    Date getCreationDate();

    List<UUID> getOwners();

    List<UUID> getMembers();

}
