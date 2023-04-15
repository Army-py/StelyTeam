package fr.army.stelyteam.utils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;

public class Member {

    private String memberName;
    private int teamRank;
    private String joinDate;
    private UUID uuid;

    public Member(String memberName, int teamRank, String joinDate, UUID uuid){
        this.memberName = memberName;
        this.teamRank = teamRank;
        this.joinDate = joinDate;
        this.uuid = uuid;
    }


    public void promoteMember(){
        this.teamRank--;
        StelyTeamPlugin.getPlugin().getDatabaseManager().promoteMember(memberName);
    }

    public void demoteMember(){
        this.teamRank++;
        StelyTeamPlugin.getPlugin().getDatabaseManager().demoteMember(memberName);
    }

    public Player asPlayer(){
        return Bukkit.getPlayer(uuid);
    }


    public String getMemberName() {
        return memberName;
    }

    public int getTeamRank() {
        return teamRank;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public void setTeamRank(int teamRank) {
        this.teamRank = teamRank;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }
}
