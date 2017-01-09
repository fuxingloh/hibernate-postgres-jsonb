package com.fxloh.hibernate.utils;

import javax.persistence.EntityManager;
import java.util.function.Consumer;

/**
 * Functional with Transaction
 * <p>
 * Created by: Fuxing
 * Date: 15/12/2016
 * Time: 4:10 AM
 * Project: hibernate-utils
 */
@FunctionalInterface
public interface Transaction extends Consumer<EntityManager>, TransactionError {

    /**
     * @param em provided entity manager
     */
    void accept(EntityManager em);

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
