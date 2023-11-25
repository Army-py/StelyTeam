package fr.army.stelyteam.controller.exception.impl;

import fr.army.stelyteam.controller.exception.ControllerException;

public class EntityManagerNotInitializedException extends ControllerException {

    public EntityManagerNotInitializedException() {
        super("Entity manager not initialized", "Entity manager not initialized");
    }
}
