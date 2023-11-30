package fr.army.stelyteam.repository.exception.impl;

import fr.army.stelyteam.repository.exception.ControllerException;

public class EntityNotFoundException extends ControllerException {

    public EntityNotFoundException(Class<?> entityClass) {
        super("Entity not found", "Entity " + entityClass.getSimpleName() + " not found");
    }
}
