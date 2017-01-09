package com.fxloh.hibernate.utils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Optional;
import java.util.function.Function;

/**
 * Functional optional Transaction
 * Similar to reduce Transaction, but will OptionalTransaction intercept NoResultException
 * and catches it and return Optional.empty()
 * <p>
 * Created by: Fuxing
 * Date: 15/12/2016
 * Time: 4:10 AM
 * Project: hibernate-utils
 */
@FunctionalInterface
public interface OptionalTransaction<T> extends Function<EntityManager, T>, TransactionError {

    /**
     * Any lazy loaded entity data must be loaded in the function
     * Once function exit, entity object will be detached
     *
     * @param em provided entity manager
     * @return any data returned in transaction
     * @throws NoResultException can throws no result exception
     */
    T apply(EntityManager em) throws NoResultException;

    /**
     * Any lazy loaded entity data must be loaded in the function
     * Once function exit, entity object will be detached
     * It catches NoResultException and return Optional.empty()
     *
     * @param em provided entity manager
     * @return Optional result
     */
    default Optional<T> optional(EntityManager em) {
        try {
            return Optional.of(apply(em));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Default error handling
     *
     * @param e exception
     * @return boolean if error is handled and should not be thrown
     */
    default boolean error(Exception e) {
        return true;
    }

}
