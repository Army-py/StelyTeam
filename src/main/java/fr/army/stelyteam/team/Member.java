package fr.army.stelyteam.team;

import fr.army.stelyteam.cache.PropertiesHolder;
import fr.army.stelyteam.cache.Property;
import fr.army.stelyteam.cache.SaveField;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.UUID;

public class Member implements PropertiesHolder {

    private final UUID playerId;
    private final Property<Integer> rank;
    private final Property<Date> joiningDate;

    public Member(@NotNull UUID playerId) {
        this.playerId = playerId;
        rank = new Property<>(SaveField.MEMBER_RANK);
        joiningDate = new Property<>(SaveField.MEMBER_JOINING_DATE);
    }

    @Override
    public @NotNull UUID getId() {
        return playerId;
    }

    public Property<Integer> getRank() {
        return rank;
    }

    public Property<Date> getJoiningDate() {
        return joiningDate;
    }

    /*
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

    public UUID getUuid() {
        return uuid;
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
 */


}
