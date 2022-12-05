package fr.army.stelyteam.utils;

public class Permission {
    
    private String permissionName;
    private int teamRank;

    public Permission(String permissionName, int teamRank){
        this.permissionName = permissionName;
        this.teamRank = teamRank;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public int getTeamRank() {
        return teamRank;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public void setTeamRank(int teamRank) {
        this.teamRank = teamRank;
    }
}
