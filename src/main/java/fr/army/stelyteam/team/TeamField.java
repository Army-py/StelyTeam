package fr.army.stelyteam.team;

public enum TeamField {

    ID,
    COMMAND_ID,
    PREFIX,
    SUFFIX,
    CREATOR,
    CREATION_DATE,
    OWNERS,
    MEMBERS;

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
