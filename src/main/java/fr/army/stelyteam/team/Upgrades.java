package fr.army.stelyteam.team;

import fr.army.stelyteam.cache.Property;

public class Upgrades {

    private final Property<Integer> members;
    private final Property<Integer> storage;

    public Upgrades() {
        members = new Property<>();
        storage = new Property<>();
    }

    public Property<Integer> getMembers() {
        return members;
    }

    public Property<Integer> getStorage() {
        return storage;
    }
}
