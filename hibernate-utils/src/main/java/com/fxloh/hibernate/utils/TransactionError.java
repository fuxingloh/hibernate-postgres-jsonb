package com.fxloh.hibernate.utils;

/**
 * Error Transaction handling
 * <p>
 * Created By: Fuxing Loh
 * Date: 3/1/2017
 * Time: 4:25 PM
 * Project: hibernate-utils
 */
@FunctionalInterface
public interface TransactionError {

    /**
     * @param exception exception to handle
     * @return boolean if should continue throw exception
     */
    boolean error(Exception exception);

}
