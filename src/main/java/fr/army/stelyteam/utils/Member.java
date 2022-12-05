package fr.army.stelyteam.utils;

public class Member {
    private String memberName;
    private int teamRank;
    private String joinDate;

    public Member(String memberName, int teamRank, String joinDate){
        this.memberName = memberName;
        this.teamRank = teamRank;
        this.joinDate = joinDate;
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
