package fr.army.stelyteam.utils;

public class Alliance {
    
    private String teamName;
    private String allianceDate;

    public Alliance(String teamName, String allianceDate){
        this.teamName = teamName;
        this.allianceDate = allianceDate;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getAllianceDate() {
        return allianceDate;
    }

    public void setAllianceName(String teamName) {
        this.teamName = teamName;
    }

    public void setAllianceDate(String allianceDate) {
        this.allianceDate = allianceDate;
    }
}

