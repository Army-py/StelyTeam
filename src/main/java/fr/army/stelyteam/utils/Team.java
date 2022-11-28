package fr.army.stelyteam.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bukkit.configuration.file.YamlConfiguration;

import fr.army.stelyteam.StelyTeamPlugin;

public class Team {
    private StelyTeamPlugin plugin = StelyTeamPlugin.getPlugin();
    private YamlConfiguration config = plugin.getConfig();

    private String teamName;
    private String teamPrefix = null;
    private String teamDescription = config.getString("team.defaultDescription");
    private Integer teamMoney = 0;
    private String creationDate = getCurrentDate();
    private int teamMembersCount = 0;
    private Integer improvLvlMembers = 0;
    private Integer teamStorageLvl = 0;
    private boolean unlockedTeamBank = false;
    private String teamOwnerName;

    public Team(String teamName, String teamPrefix, String teamDescription, int teamMoney, String creationDate, int teamMembersCount, int improvLvlMembers, int teamStorageLvl, boolean unlockedTeamBank, String teamOwnerName){
        this.teamName = teamName;
        this.teamPrefix = teamPrefix;
        this.teamDescription = teamDescription;
        this.teamMoney = teamMoney;
        this.creationDate = creationDate;
        this.teamMembersCount = teamMembersCount;
        this.improvLvlMembers = improvLvlMembers;
        this.teamStorageLvl = teamStorageLvl;
        this.unlockedTeamBank = unlockedTeamBank;
        this.teamOwnerName = teamOwnerName;
    }

    public Team(String teamName, String teamPrefix, String creationDate, String teamOwnerName){
        this.teamName = teamName;
        this.teamPrefix = teamPrefix;
        this.teamOwnerName = teamOwnerName;
    }

    public Team(String teamName, String teamOwnerName){
        this.teamName = teamName;
        this.teamOwnerName = teamOwnerName;
    }


    public String getTeamName() {
        return teamName;
    }

    public String getTeamPrefix() {
        return teamPrefix;
    }

    public String getTeamDescription() {
        return teamDescription;
    }

    public Integer getTeamMoney() {
        return teamMoney;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public int getTeamMembersCount() {
        return teamMembersCount;
    }

    public Integer getImprovLvlMembers() {
        return improvLvlMembers;
    }

    public Integer getTeamStorageLvl() {
        return teamStorageLvl;
    }

    public boolean isUnlockedTeamBank() {
        return unlockedTeamBank;
    }

    public String getTeamOwnerName() {
        return teamOwnerName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setTeamPrefix(String teamPrefix) {
        this.teamPrefix = teamPrefix;
    }

    public void setTeamDescription(String teamDescription) {
        this.teamDescription = teamDescription;
    }

    public void setTeamMoney(Integer teamMoney) {
        this.teamMoney = teamMoney;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public void setTeamMembersCount(int teamMembersCount) {
        this.teamMembersCount = teamMembersCount;
    }

    public void setImprovLvlMembers(Integer improvLvlMembers) {
        this.improvLvlMembers = improvLvlMembers;
    }

    public void setTeamStorageLvl(Integer teamStorageLvl) {
        this.teamStorageLvl = teamStorageLvl;
    }

    public void setUnlockedTeamBank(boolean unlockedTeamBank) {
        this.unlockedTeamBank = unlockedTeamBank;
    }

    public void setTeamOwnerName(String teamOwnerName) {
        this.teamOwnerName = teamOwnerName;
    }

    private String getCurrentDate(){
        return new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
    }
}
