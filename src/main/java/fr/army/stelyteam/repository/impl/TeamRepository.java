package fr.army.stelyteam.repository.impl;

import fr.army.stelyteam.entity.impl.MemberEntity;
import fr.army.stelyteam.entity.impl.TeamEntity;
import fr.army.stelyteam.repository.AbstractRepository;
import fr.army.stelyteam.repository.callback.AsyncCallBackObject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class TeamRepository extends AbstractRepository<TeamEntity> {

    public TeamRepository(Class<TeamEntity> entityClass, EntityManager entityManager) {
        super(entityClass, entityManager);
    }

    public TeamEntity findByTeamUuid(@NotNull UUID uuid){
        CriteriaQuery<TeamEntity> query = criteriaBuilder.createQuery(entityClass);
        Root<TeamEntity> teamEntityRoot = query.from(entityClass);
        query.select(teamEntityRoot);
        query.where(criteriaBuilder.equal(teamEntityRoot.get("uuid"), uuid));
        List<TeamEntity> results = entityManager.createQuery(query).getResultList();
        return !results.isEmpty() ? results.get(0) : null;
    }

    public synchronized void asyncFindByTeamUuid(@NotNull UUID uuid,
                                                 @NotNull AsyncCallBackObject<TeamEntity> callback){
        executeAsyncQuery(() -> findByTeamUuid(uuid), callback);
    }

    public TeamEntity findByTeamName(@NotNull String name){
        CriteriaQuery<TeamEntity> query = criteriaBuilder.createQuery(entityClass);
        Root<TeamEntity> teamEntityRoot = query.from(entityClass);
        query.select(teamEntityRoot);
        query.where(criteriaBuilder.equal(teamEntityRoot.get("name"), name));
        List<TeamEntity> results = entityManager.createQuery(query).getResultList();
        return !results.isEmpty() ? results.get(0) : null;
    }

    public synchronized void asyncFindByTeamName(@NotNull String teamName,
                                                 @NotNull AsyncCallBackObject<TeamEntity> callback) {
        executeAsyncQuery(() -> findByTeamName(teamName), callback);
    }


    @Nullable
    public TeamEntity findByPlayerName(@NotNull String playerName){
        CriteriaQuery<TeamEntity> query = criteriaBuilder.createQuery(entityClass);
        Root<TeamEntity> teamEntityRoot = query.from(entityClass);
        Join<TeamEntity, MemberEntity> teamMemberEntityJoin = teamEntityRoot.join("memberEntities");
        query.select(teamEntityRoot);
        query.where(criteriaBuilder.equal(teamMemberEntityJoin.get("name"), playerName));
        List<TeamEntity> results = entityManager.createQuery(query).getResultList();
        return !results.isEmpty() ? results.get(0) : null;
    }

    public synchronized void asyncFindByPlayerName(@NotNull String playerName,
                                                   @NotNull AsyncCallBackObject<TeamEntity> callback) {
        executeAsyncQuery(() -> findByPlayerName(playerName), callback);
    }
}
