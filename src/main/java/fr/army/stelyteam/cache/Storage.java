package fr.army.stelyteam.cache;

public interface Storage {
    <T> T retrieve(Property<T> properties);
}
