package fr.army.stelyteam.repository.impl;

import fr.army.stelyteam.repository.AbstractRepository;
import fr.army.stelyteam.entity.impl.TeamEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.jetbrains.annotations.NotNull;

public class TeamRepository extends AbstractRepository<TeamEntity> {

    public TeamRepository(Class<TeamEntity> entityClass, EntityManager entityManager) {
        super(entityClass, entityManager);
    }


    public TeamEntity findByName(@NotNull String teamName) {

        CriteriaQuery<TeamEntity> query = criteriaBuilder.createQuery(entityClass);
        Root<TeamEntity> teamEntityRoot = query.from(entityClass);
        query.select(teamEntityRoot);
        query.where(criteriaBuilder.equal(teamEntityRoot.get("name"), teamName));
        return entityManager.createQuery(query).getSingleResult();
    }
}
