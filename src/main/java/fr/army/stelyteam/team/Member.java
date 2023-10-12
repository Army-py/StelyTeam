package fr.army.stelyteam.team;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.config.Config;

public class Member {

    private String memberName;
    private Rank rank;
    private String joinDate;
    private UUID uuid;

    public Member(String memberName, Rank rank, String joinDate, UUID uuid){
        this.memberName = memberName;
        this.rank = rank;
        this.joinDate = joinDate;
        this.uuid = uuid;
    }


    public void promoteMember(){
        // this.teamRank--; //TODO : revoir
        this.rank = Config.ranks.get(Config.ranks.indexOf(this.rank) - 1);
        StelyTeamPlugin.getPlugin().getDatabaseManager().promoteMember(memberName);
    }

    public void demoteMember(){
        // this.teamRank++; // TODO: revoir
        this.rank = Config.ranks.get(Config.ranks.indexOf(this.rank) + 1);
        StelyTeamPlugin.getPlugin().getDatabaseManager().demoteMember(memberName);
    }

    public Player asPlayer(){
        return Bukkit.getPlayer(uuid);
    }


    public String getMemberName() {
        return memberName;
    }

    public Rank getTeamRank() {
        return rank;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }
}
