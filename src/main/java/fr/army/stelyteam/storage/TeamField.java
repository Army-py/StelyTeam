package fr.army.stelyteam.storage;

import fr.army.stelyteam.api.LazyLocation;
import fr.army.stelyteam.storage.mapper.FieldExtractor;
import fr.army.stelyteam.storage.mapper.FieldInflater;
import fr.army.stelyteam.storage.mapper.PlayerMapper;
import fr.army.stelyteam.team.Team;

import java.util.Date;
import java.util.UUID;

public enum TeamField {

    COMMAND_ID(Team::getCommandId, (o, t) -> t.getUnsafe().setCommandId((String) o)),
    PREFIX(Team::getPrefix, (o, t) -> t.getUnsafe().setPrefix((String) o)),
    SUFFIX(Team::getSuffix, (o, t) -> t.getUnsafe().setSuffix((String) o)),
    CREATOR(Team::getCreator, (o, t) -> t.getUnsafe().setCreator((UUID) o)),
    CREATION_DATE(Team::getCreationDate, (o, t) -> t.getUnsafe().setCreationDate((Date) o)),
    LEVEL(t -> t.getPerks().getLevel(), (o, t) -> t.getPerks().getUnsafe().setLevel((int) o)),
    HOME(t -> t.getPerks().getHome(), (o, t) -> t.getPerks().getUnsafe().setHome((LazyLocation) o)),
    BANK_ACCOUNT(t -> t.getPerks().getBankAccount().isEnable(), (o, t) -> t.getPerks().getBankAccount().getUnsafe().setEnable((boolean) o)),
    MONEY(t -> t.getPerks().getBankAccount().getMoney(), (o, t) -> t.getPerks().getBankAccount().getUnsafe().setMoney((double) o)),
    PLAYERS(new PlayerMapper(), new PlayerMapper());

    private final FieldExtractor extractor;
    private final FieldInflater inflater;

    TeamField(FieldExtractor extractor, FieldInflater inflater) {
        this.extractor = extractor;
        this.inflater = inflater;
    }

    public FieldExtractor getExtractor() {
        return extractor;
    }

    public FieldInflater getInflater() {
        return inflater;
    }

    /**
     * Set this field dirty
     *
     * @param dirtyFields The dirty value of a {@link Team}
     * @return A dirty value that tells that this field is dirty
     */
    public int setDirty(int dirtyFields) {
        return dirtyFields | 1 << ordinal();
    }

    /**
     * The dirty state of this field
     *
     * @param dirtyFields The dirty value of a {@link Team}
     * @return {@code true} if this field is dirty. {@code false} otherwise
     */
    public boolean isDirty(int dirtyFields) {
        return (dirtyFields >> ordinal() & 1) == 1;
    }

}
