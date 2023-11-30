package fr.army.stelyteam.repository.exception.impl;

import fr.army.stelyteam.repository.exception.RepositoryException;

public class EntityManagerNotInitializedException extends RepositoryException {

    public EntityManagerNotInitializedException() {
        super("Entity manager not initialized", "Entity manager not initialized");
    }
}
