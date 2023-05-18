package fr.army.stelyteam.team;

import java.util.UUID;

public class Alliance {
    
    private UUID teamUuid;
    private String allianceDate;

    public Alliance(UUID teamUuid, String allianceDate){
        this.teamUuid = teamUuid;
        this.allianceDate = allianceDate;
    }

    public UUID getTeamUuid() {
        return teamUuid;
    }

    public String getAllianceDate() {
        return allianceDate;
    }

    public void setTeam(UUID teamUuid) {
        this.teamUuid = teamUuid;
    }

    public void setAllianceDate(String allianceDate) {
        this.allianceDate = allianceDate;
    }
}

