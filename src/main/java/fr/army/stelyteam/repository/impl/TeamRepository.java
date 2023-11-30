package fr.army.stelyteam.repository.impl;

import fr.army.stelyteam.entity.impl.MemberEntity;
import fr.army.stelyteam.repository.AbstractRepository;
import fr.army.stelyteam.entity.impl.TeamEntity;
import fr.army.stelyteam.repository.callback.AsyncCallBackObject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import org.jetbrains.annotations.NotNull;

public class TeamRepository extends AbstractRepository<TeamEntity> {

    public TeamRepository(Class<TeamEntity> entityClass, EntityManager entityManager) {
        super(entityClass, entityManager);
    }


    public synchronized void findByTeamName(@NotNull String teamName, AsyncCallBackObject<TeamEntity> callback) {
        executeQuery(() -> {
                    CriteriaQuery<TeamEntity> query = criteriaBuilder.createQuery(entityClass);
                    Root<TeamEntity> teamEntityRoot = query.from(entityClass);
                    query.select(teamEntityRoot);
                    query.where(criteriaBuilder.equal(teamEntityRoot.get("name"), teamName));
                    return entityManager.createQuery(query).getSingleResult();
                },
                callback);
    }


    public synchronized void findByPlayerName(@NotNull String playerName, AsyncCallBackObject<TeamEntity> callback) {
        executeQuery(() -> {
                    CriteriaQuery<TeamEntity> query = criteriaBuilder.createQuery(entityClass);
                    Root<TeamEntity> teamEntityRoot = query.from(entityClass);
                    Join<TeamEntity, MemberEntity> teamMemberEntityJoin = teamEntityRoot.join("memberEntities");
                    query.select(teamEntityRoot);
                    query.where(criteriaBuilder.equal(teamMemberEntityJoin.get("name"), playerName));
                    return entityManager.createQuery(query).getSingleResult();
                },
                callback);
    }
}
