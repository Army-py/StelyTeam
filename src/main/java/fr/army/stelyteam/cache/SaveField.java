package fr.army.stelyteam.cache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum SaveField {

    NAME(SaveFieldHolder.TEAM, "teamName"),
    PREFIX(SaveFieldHolder.TEAM, "teamPrefix"),
    DESCRIPTION(SaveFieldHolder.TEAM, "teamDescription"),
    CREATION_DATE(SaveFieldHolder.TEAM, "creationDate"),
    MEMBERS(SaveFieldHolder.TEAM, null),
    MEMBER_RANK(SaveFieldHolder.MEMBER, "teamRank"),
    MEMBER_JOINING_DATE(SaveFieldHolder.MEMBER, "joinDate"),
    UPGRADES_MEMBERS(SaveFieldHolder.TEAM, "upgradesLvlMembers"),
    BANK_UNLOCKED(SaveFieldHolder.TEAM, "unlockedTeamBank"),
    BANK_BALANCE(SaveFieldHolder.TEAM, "teamMoney"),
    TEAM_UUID(SaveFieldHolder.TEAM, "teamUuid"),
    MEMBER_UUID(SaveFieldHolder.MEMBER, "playerUuid");

    private final SaveFieldHolder holder;
    private final String columnName;

    SaveField(@NotNull SaveFieldHolder holder, @Nullable String columnName) {
        this.holder = holder;
        this.columnName = columnName;
    }

    public SaveFieldHolder getHolder() {
        return holder;
    }

    public String getColumnName(){
        return columnName;
    }

}
