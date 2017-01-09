package org.hibernate.dialect;

import java.sql.Types;

/**
 * Created By: Fuxing Loh
 * Date: 5/1/2017
 * Time: 3:56 PM
 * Project: hibernate-postgres-jsonb
 */
public class JsonPostgreSQLDialect extends PostgreSQL82Dialect {

    /**
     * Must use this dialect
     * Register jsonb as JAVA_OBJECT
     */
    public JsonPostgreSQLDialect() {
        super(); // Extends PostgreSQL82Dialect
        registerColumnType(Types.JAVA_OBJECT, "jsonb");
    }
}
