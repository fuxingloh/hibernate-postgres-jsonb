
package com.fxloh.hibernate.utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Optional;

/**
 * Transaction provider to run lambda function in JPA style
 * <p>
 * Created by Fuxing
 * Date: 8/7/2015
 * Time: 4:07 PM
 * Project: hibernate-utils
 */
public class TransactionProvider {

    private EntityManagerFactory factory;

    /**
     * @param factory for provider to create entity manager
     */
    public TransactionProvider(EntityManagerFactory factory) {
        this.factory = factory;
    }

    /**
     * @return provided EntityFactoryFactory
     */
    public EntityManagerFactory getFactory() {
        return factory;
    }

    /**
     * @return boolean indicating whether the provider is open
     */
    public boolean isOpen() {
        return getFactory().isOpen();
    }

    /**
     * Run JPA style transaction in lambda
     *
     * @param transaction transaction lambda
     */
    public void with(Transaction transaction) {
        with(transaction, transaction);
    }

    /**
     * Run JPA style transaction in lambda
     *
     * @param transaction transaction lambda
     * @param error       error lambda to run if error is thrown
     */
    public void with(Transaction transaction, TransactionError error) {
        // Create and start
        EntityManager entityManager = factory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            // Run
            transaction.accept(entityManager);
            // Close
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

            // Transaction Error
            if (error.error(e)) {
                throw e;
            }
        } finally {
            entityManager.close();
        }
    }

    /**
     * Run JPA style transaction in functional style with reduce
     *
     * @param reduceTransaction reduce transaction to apply
     * @param <T>               type of object
     * @return object
     */
    public <T> T reduce(ReduceTransaction<T> reduceTransaction) {
        return reduce(reduceTransaction, reduceTransaction);
    }

    /**
     * Run JPA style transaction in functional style with reduce
     *
     * @param reduceTransaction reduce transaction to apply
     * @param error             error lambda to run if error is thrown
     * @param <T>               type of object
     * @return object
     */
    public <T> T reduce(ReduceTransaction<T> reduceTransaction, TransactionError error) {
        T object = null;
        // Create and start
        EntityManager entityManager = factory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            // Run
            object = reduceTransaction.apply(entityManager);
            // Close
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

            // Transaction Error
            if (error.error(e)) {
                throw e;
            }
        } finally {
            entityManager.close();
        }
        return object;
    }

    /**
     * Run jpa style transaction in functional style with optional transaction
     * Optional Transaction are basically reduce transaction that will
     * catch NoResultException and convert it to Optional.empty()
     * Using the default transaction provider
     *
     * @param optionalTransaction reduce transaction to apply that with convert to optional
     * @param <T>                 type of object
     * @return object
     */
    public <T> Optional<T> optional(OptionalTransaction<T> optionalTransaction) {
        return reduce(optionalTransaction::optional, optionalTransaction);
    }

    /**
     * Run jpa style transaction in functional style with optional transaction
     * Optional Transaction are basically reduce transaction that will
     * catch NoResultException and convert it to Optional.empty()
     * Using the default transaction provider
     *
     * @param optionalTransaction reduce transaction to apply that with convert to optional
     * @param error               lambda to run if error is thrown
     * @param <T>                 type of object
     * @return object
     */
    public <T> Optional<T> optional(OptionalTransaction<T> optionalTransaction, TransactionError error) {
        return reduce(optionalTransaction::optional, error);
    }
}
