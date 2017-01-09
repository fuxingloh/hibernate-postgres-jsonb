package com.fxloh.hibernate.utils;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Thread-Safe Singleton Hibernate Util
 * Only use this if there is no default implementation available on your platform.
 * <p>
 * Created by Fuxing
 * Date: 1/1/2015
 * Time: 5:49 PM
 * Project: hibernate-utils
 */
public final class HibernateUtils {
    public static final String DEFAULT_PERSISTENCE_UNIT = "defaultPersistenceUnit";

    private static Map<String, TransactionProvider> providers = new HashMap<>();

    private HibernateUtils() {/* NOT Suppose to init */}

    /**
     * @param properties nullable properties for overriding
     * @return created TransactionProvider
     */
    public static TransactionProvider setupFactory(Map<String, String> properties) {
        return setupFactory(DEFAULT_PERSISTENCE_UNIT, properties);
    }

    /**
     * @param unitName   persistence unit name
     * @param properties nullable properties for overriding
     * @return created TransactionProvider
     */
    public static TransactionProvider setupFactory(String unitName, Map<String, String> properties) {
        if (!providers.containsKey(unitName)) {
            synchronized (HibernateUtils.class) {
                if (!providers.containsKey(unitName)) {
                    // Setup Factory & Provider
                    EntityManagerFactory factory = Persistence.createEntityManagerFactory(unitName, properties);
                    TransactionProvider provider = new TransactionProvider(factory);
                    // Put to Map
                    providers.put(unitName, provider);
                    return provider;
                }
            }
        }
        throw new RuntimeException(new IllegalStateException("Factory already initialized."));
    }

    /**
     * Shutdown the default instance
     * Thread-safe
     */
    public static void shutdown() {
        shutdown(DEFAULT_PERSISTENCE_UNIT);
    }

    /**
     * Shutdown the default instance
     * Thread-safe
     */
    public static void shutdown(String unitName) {
        if (providers.containsKey(unitName)) {
            synchronized (HibernateUtils.class) {
                if (providers.containsKey(unitName)) {
                    providers.remove(unitName).getFactory().close();
                }
            }
        }
    }

    /**
     * Shutdown all factory
     */
    public static void shutdownAll() {
        for (String unitName : providers.keySet()) {
            shutdown(unitName);
        }
    }

    /**
     * @param unitName persistence unit name
     * @return TransactionProvider of unit, null if don't exist
     */
    public static TransactionProvider get(String unitName) {
        return providers.get(unitName);
    }

    /**
     * @return default TransactionProvider
     */
    public static TransactionProvider get() {
        return get(DEFAULT_PERSISTENCE_UNIT);
    }

    /**
     * Using the default transaction provider
     *
     * @param transaction transaction to apply
     */
    public static void with(Transaction transaction) {
        get().with(transaction);
    }

    /**
     * Run jpa style transaction in functional style with reduce
     * Using the default transaction provider
     *
     * @param reduceTransaction reduce transaction to apply
     * @param <T>               type of object
     * @return object
     */
    public static <T> T reduce(ReduceTransaction<T> reduceTransaction) {
        return get().reduce(reduceTransaction);
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
    public static <T> Optional<T> optional(OptionalTransaction<T> optionalTransaction) {
        return get().optional(optionalTransaction);
    }
}

