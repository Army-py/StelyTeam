package fr.army.stelyteam.storage;

public interface ChangeTracked {

    boolean isDirty();

    void setDirty(boolean dirty);

}
