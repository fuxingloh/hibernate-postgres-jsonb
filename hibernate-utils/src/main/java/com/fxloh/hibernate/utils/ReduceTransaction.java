package com.fxloh.hibernate.utils;

import javax.persistence.EntityManager;
import java.util.function.Function;

/**
 * Functional reduce Transaction
 * <p>
 * Created by: Fuxing
 * Date: 15/12/2016
 * Time: 4:10 AM
 * Project: hibernate-utils
 */
@FunctionalInterface
public interface ReduceTransaction<T> extends Function<EntityManager, T>, TransactionError {

    /**
     * Any lazy loaded entity data must be loaded in the function
     * Once function exit, entity object will be detached
     *
     * @param em provided entity manager
     * @return any data returned in transaction
     */
    T apply(EntityManager em);

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
