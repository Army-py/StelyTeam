package fr.army.stelyteam.controller;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.controller.callback.AsyncCallBackExceptionHandler;
import fr.army.stelyteam.controller.callback.AsyncCallBackObject;
import fr.army.stelyteam.controller.callback.AsyncCallBackObjectList;
import fr.army.stelyteam.controller.exception.ControllerException;
import fr.army.stelyteam.controller.exception.impl.EntityManagerNotInitializedException;
import fr.army.stelyteam.controller.exception.impl.EntityNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Properties;

public abstract class AbstractController<T> {

    private final Class<T> entityClass;
    private static EntityManagerFactory entityManagerFactory;

    private static final boolean remoteDatabase = false;
    private static final String databaseHost = "localhost";
    private static final int databasePort = 3306;
    private static final String databaseSchema = "schema";
    private static final String databaseUsername = "username";
    private static final String databasePassword = "password";

    public AbstractController(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Transactional
    protected synchronized void persist(T entity) throws ControllerException {
        EntityManager entityManager = getEntityManager();
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
    protected synchronized T merge(T entity) throws ControllerException {
        EntityManager entityManager = getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        entityManager.merge(entity);

        entityTransaction.commit();
        entityManager.close();
        return entity;
    }

    public synchronized void edit(T entity, AsyncCallBackObject<T> asyncCallBackObject, AsyncCallBackExceptionHandler asyncCallBackExceptionHandler) {
        new Thread(() -> {
            try {
                asyncCallBackObject.done(merge(entity));
            } catch (ControllerException e) {
                asyncCallBackExceptionHandler.error(e);
            }
        }).start();
    }

    @Transactional
    protected synchronized void remove(T entity) throws ControllerException {
        EntityManager entityManager = getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        entityManager.remove(entityManager.merge(entity));

        entityTransaction.commit();
        entityManager.close();
    }

    public void remove(T entity, AsyncCallBackExceptionHandler asyncCallBackExceptionHandler) {
        new Thread(() -> {
            try {
                remove(entity);
            } catch (ControllerException e) {
                asyncCallBackExceptionHandler.error(e);
            }
        }).start();
    }

    protected synchronized T find(Object id) throws ControllerException {
        EntityManager entityManager = getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        T e = entityManager.find(entityClass, id);

        entityTransaction.commit();
        entityManager.close();
        if (e == null) {
            throw new EntityNotFoundException(getClass());
        }
        return e;
    }

    public void find(Object id, AsyncCallBackObject<T> asyncCallBackObject, AsyncCallBackExceptionHandler asyncCallBackExceptionHandler) {
        new Thread(() -> {
            try {
                asyncCallBackObject.done(find(id));
            } catch (ControllerException e) {
                asyncCallBackExceptionHandler.error(e);
            }
        }).start();
    }

    protected synchronized List<T> findAll() throws ControllerException {
        EntityManager entityManager = getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
        cq.select(cq.from(entityClass));
        List<T> e = getEntityManager().createQuery(cq).getResultList();

        entityTransaction.commit();
        entityManager.close();
        return e;
    }

    public void findAll(AsyncCallBackObjectList<T> asyncCallBackObjectList, AsyncCallBackExceptionHandler asyncCallBackExceptionHandler) {
        new Thread(() -> {
            try {
                asyncCallBackObjectList.done(findAll());
            } catch (ControllerException e) {
                asyncCallBackExceptionHandler.error(e);
            }
        }).start();
    }

    protected EntityManager getEntityManager() throws ControllerException {
        setupEntityManagerFactory();
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            return entityManagerFactory.createEntityManager();
        } else {
            throw new EntityManagerNotInitializedException();
        }
    }

    private void setupEntityManagerFactory() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            Properties properties = new Properties();

            if (remoteDatabase) {
                properties.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
                properties.put("javax.persistence.jdbc.url", "jdbc:mysql://" + databaseHost + ":" + databasePort + "/" + databaseSchema + "?autoReconnect=true");
                properties.put("javax.persistence.jdbc.user", databaseUsername);
                properties.put("javax.persistence.jdbc.password", databasePassword);
                properties.put("hibernate.dialect", "org.hibernate.dialect.MariaDBDialect");
            } else {
                properties.put("javax.persistence.jdbc.driver", "org.sqlite.JDBC");
                properties.put("javax.persistence.jdbc.url", "jdbc:sqlite:./" + StelyTeamPlugin.getPlugin().getDataFolder().getPath() + "/database" + "?autoReconnect=true");
                properties.put("hibernate.dialect", "org.hibernate.dialect.SQLiteDialect");
            }

            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            entityManagerFactory = Persistence.createEntityManagerFactory("persistence-unit", properties);
        }
    }

    public static void closeEntityManagerFactory() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}
