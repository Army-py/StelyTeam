package fr.army.stelyteam.cache;

import org.jetbrains.annotations.NotNull;

public enum SaveField {

    NAME(SaveFieldHolder.TEAM),
    DISPLAY_NAME(SaveFieldHolder.TEAM),
    DESCRIPTION(SaveFieldHolder.TEAM),
    CREATION_DATE(SaveFieldHolder.TEAM),
    OWNER(SaveFieldHolder.TEAM),
    MEMBERS(SaveFieldHolder.TEAM),
    MEMBER_RANK(SaveFieldHolder.MEMBER),
    MEMBER_JOINING_DATE(SaveFieldHolder.MEMBER),
    UPGRADES_MEMBERS(SaveFieldHolder.TEAM),
    UPGRADES_STORAGES(SaveFieldHolder.TEAM),
    BANK_UNLOCKED(SaveFieldHolder.TEAM),
    BANK_BALANCE(SaveFieldHolder.TEAM),
    TEAM_UUID(SaveFieldHolder.TEAM),
    MEMBER_UUID(SaveFieldHolder.MEMBER),
    PERMISSION_NAME(SaveFieldHolder.PERMISSION),
    ALLIANCE_UUID(SaveFieldHolder.ALLIANCE),
    STORAGE_ID(SaveFieldHolder.STORAGE),
    MEMBER_NAME(SaveFieldHolder.MEMBER),
    PERMISSION_RANK(SaveFieldHolder.PERMISSION);

    private final SaveFieldHolder holder;

    SaveField(@NotNull SaveFieldHolder holder) {
        this.holder = holder;
    }

    public SaveFieldHolder getHolder() {
        return holder;
    }

}
