package fr.army.stelyteam.team;

import fr.army.stelyteam.cache.Property;
import fr.army.stelyteam.cache.SaveField;

public class Upgrades {

    private final Property<Integer> members;
    private final Property<Integer> storages;

    public Upgrades() {
        members = new Property<>(SaveField.UPGRADES_MEMBERS);
        storages = new Property<>(SaveField.UPGRADES_STORAGES);
    }

    public Property<Integer> getMembers() {
        return members;
    }

    public Property<Integer> getStorage() {
        return storages;
    }
}
