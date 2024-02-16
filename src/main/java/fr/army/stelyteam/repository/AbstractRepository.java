package fr.army.stelyteam.repository;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.repository.callback.AsyncCallBackObject;
import fr.army.stelyteam.repository.callback.AsyncCallBackObjectList;
import fr.army.stelyteam.repository.exception.RepositoryException;
import fr.army.stelyteam.repository.exception.impl.EntityNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public abstract class AbstractRepository<T> {

    protected final Class<T> entityClass;
    protected final EntityManager entityManager;
    protected final CriteriaBuilder criteriaBuilder;

    public AbstractRepository(Class<T> entityClass, EntityManager entityManager) {
        this.entityClass = entityClass;
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    @Transactional
    protected synchronized void persist(T entity) throws RepositoryException {
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        entityManager.persist(entity);

        entityTransaction.commit();
        entityManager.close();
    }

    public synchronized void insert(T entity) {
        new Thread(() -> {
            try {
                persist(entity);
            } catch (RepositoryException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Transactional
    protected synchronized T merge(T entity) {
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        T mergedEntity = entityManager.merge(entity);

        entityTransaction.commit();
        entityManager.close();
        return mergedEntity;
    }

    public synchronized void update(T entity, AsyncCallBackObject<T> asyncCallBackObject) {
        new Thread(() -> {
            asyncCallBackObject.done(merge(entity));
        }).start();
    }

    @Transactional
    protected synchronized void remove(T entity) {
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        entityManager.remove(entityManager.merge(entity));

        entityTransaction.commit();
        entityManager.close();
    }

    public void delete(T entity) {
        new Thread(() -> {
            remove(entity);
        }).start();
    }

    protected synchronized T find(int id) throws RepositoryException {
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        T e = entityManager.find(entityClass, id);

        entityTransaction.commit();
        entityManager.close();
        if (e == null) {
            throw new EntityNotFoundException(entityClass);
        }
        return e;
    }

    public void find(int id, AsyncCallBackObject<T> asyncCallBackObject) {
        new Thread(() -> {
            try {
                asyncCallBackObject.done(find(id));
            } catch (RepositoryException e) {
                StelyTeamPlugin.getPlugin().getLogger().severe(e.getMessage());
            }
        }).start();
    }

    protected synchronized List<T> findAll() throws RepositoryException {
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        CriteriaQuery<T> cq = entityManager.getCriteriaBuilder().createQuery(entityClass);
        cq.select(cq.from(entityClass));
        List<T> e = entityManager.createQuery(cq).getResultList();
        if (e.isEmpty()){
            throw new EntityNotFoundException(entityClass);
        }

        entityTransaction.commit();
        entityManager.close();
        return e;
    }

    public void findAll(AsyncCallBackObjectList<T> asyncCallBackObjectList) {
        new Thread(() -> {
            try {
                asyncCallBackObjectList.done(findAll());
            } catch (RepositoryException e) {
                StelyTeamPlugin.getPlugin().getLogger().severe(e.getMessage());
            }
        }).start();
    }

    protected void executeAsyncQuery(Supplier<T> supplier, AsyncCallBackObject<T> asyncCallBackObject){
        new Thread(() -> {
            asyncCallBackObject.done(supplier.get());
        }).start();
    }

    protected void executeAsyncQueryList(Supplier<List<T>> supplier, AsyncCallBackObjectList<T> asyncCallBackObjectList){
        new Thread(() -> {
            asyncCallBackObjectList.done(supplier.get());
        }).start();
    }

    public T getReference(UUID id) {
        return entityManager.getReference(entityClass, id);
    }
}
