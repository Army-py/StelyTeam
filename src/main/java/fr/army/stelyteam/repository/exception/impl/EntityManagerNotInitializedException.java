package fr.army.stelyteam.repository.exception.impl;

import fr.army.stelyteam.repository.exception.ControllerException;

public class EntityManagerNotInitializedException extends ControllerException {

    public EntityManagerNotInitializedException() {
        super("Entity manager not initialized", "Entity manager not initialized");
    }
}
