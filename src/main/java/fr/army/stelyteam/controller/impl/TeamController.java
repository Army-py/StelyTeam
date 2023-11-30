package fr.army.stelyteam.controller.impl;

import fr.army.stelyteam.controller.AbstractController;
import fr.army.stelyteam.entity.impl.TeamEntity;
import jakarta.persistence.EntityManager;

public class TeamController extends AbstractController<TeamEntity> {
    public TeamController(Class<TeamEntity> entityClass, EntityManager entityManager) {
        super(entityClass, entityManager);
    }
}
