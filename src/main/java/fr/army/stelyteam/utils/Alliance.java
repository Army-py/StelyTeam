package fr.army.stelyteam.utils;

public class Alliance {
    
    private String allianceName;
    private String allianceDate;

    public Alliance(String allianceName, String allianceDate){
        this.allianceName = allianceName;
        this.allianceDate = allianceDate;
    }

    public String getAllianceName() {
        return allianceName;
    }

    public String getAllianceDate() {
        return allianceDate;
    }

    public void setAllianceName(String allianceName) {
        this.allianceName = allianceName;
    }

    public void setAllianceDate(String allianceDate) {
        this.allianceDate = allianceDate;
    }
}
