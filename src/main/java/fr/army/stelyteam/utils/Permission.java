package fr.army.stelyteam.utils;

public class Permission {
    
    private String permissionName;
    private int teamRank;

    public Permission(String permissionName, int teamRank){
        this.permissionName = permissionName;
        this.teamRank = teamRank;
    }


    public void incrementTeamRank(){
        this.teamRank++;
    }


    public void decrementTeamRank(){
        this.teamRank--;
    }


    public String getPermissionName() {
        return permissionName;
    }

    public int getTeamRank() {
        return teamRank;
    }
}
