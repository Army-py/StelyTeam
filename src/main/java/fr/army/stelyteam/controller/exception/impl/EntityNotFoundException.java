package fr.army.stelyteam.controller.exception.impl;

import fr.army.stelyteam.controller.exception.ControllerException;

public class EntityNotFoundException extends ControllerException {

    public EntityNotFoundException(Class<?> entityClass) {
        super("Entity not found", "Entity " + entityClass.getSimpleName() + " not found");
    }
}
