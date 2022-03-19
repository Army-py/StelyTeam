package fr.army.stelyteam.api;

import java.util.Date;
import java.util.UUID;

public interface ITeam {

    UUID getId();

    String getCommandId();

    String getPrefix();

    String getSuffix();

    UUID getCreator();

    Date getCreationDate();

    IPlayerList getOwners();

    IPlayerList getMembers();

}
