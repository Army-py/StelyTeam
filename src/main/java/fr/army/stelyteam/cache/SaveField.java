package fr.army.stelyteam.cache;

import org.jetbrains.annotations.NotNull;

public enum SaveField {

    NAME(SaveFieldHolder.TEAM),
    PREFIX(SaveFieldHolder.TEAM),
    DESCRIPTION(SaveFieldHolder.TEAM),
    CREATION_DATE(SaveFieldHolder.TEAM),
    OWNER(SaveFieldHolder.TEAM),
    MEMBERS(SaveFieldHolder.TEAM),
    MEMBER_RANK(SaveFieldHolder.MEMBER),
    MEMBER_JOINING_DATE(SaveFieldHolder.MEMBER),
    UPGRADES_MEMBERS(SaveFieldHolder.TEAM),
    BANK_UNLOCKED(SaveFieldHolder.TEAM),
    BANK_BALANCE(SaveFieldHolder.TEAM);

    private final SaveFieldHolder holder;

    SaveField(@NotNull SaveFieldHolder holder) {
        this.holder = holder;
    }

    public SaveFieldHolder getHolder() {
        return holder;
    }

}
