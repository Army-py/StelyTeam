package fr.army.stelyteam.controller;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.controller.callback.AsyncCallBackObject;
import fr.army.stelyteam.controller.callback.AsyncCallBackObjectList;
import fr.army.stelyteam.controller.exception.ControllerException;
import fr.army.stelyteam.controller.exception.impl.EntityNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.transaction.Transactional;

import java.util.List;

public abstract class AbstractController<T> {

    protected final Class<T> entityClass;
    protected final EntityManager entityManager;
    protected final CriteriaBuilder criteriaBuilder;

    public AbstractController(Class<T> entityClass, EntityManager entityManager) {
        this.entityClass = entityClass;
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    @Transactional
    protected synchronized void persist(T entity) throws ControllerException {
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        entityManager.persist(entity);

        entityTransaction.commit();
        entityManager.close();
    }

    public synchronized void create(T entity) {
        new Thread(() -> {
            try {
                persist(entity);
            } catch (ControllerException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Transactional
    protected synchronized T merge(T entity) {
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        entityManager.merge(entity);

        entityTransaction.commit();
        entityManager.close();
        return entity;
    }

    public synchronized void edit(T entity, AsyncCallBackObject<T> asyncCallBackObject) {
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

    protected synchronized T find(Object id) throws ControllerException {
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

    public void find(Object id, AsyncCallBackObject<T> asyncCallBackObject) {
        new Thread(() -> {
            try {
                asyncCallBackObject.done(find(id));
            } catch (ControllerException e) {
                StelyTeamPlugin.getPlugin().getLogger().severe(e.getMessage());
            }
        }).start();
    }

    protected synchronized List<T> findAll() throws ControllerException {
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
            } catch (ControllerException e) {
                StelyTeamPlugin.getPlugin().getLogger().severe(e.getMessage());
            }
        }).start();
    }
}
