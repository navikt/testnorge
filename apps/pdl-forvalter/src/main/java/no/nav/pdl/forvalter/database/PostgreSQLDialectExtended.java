package no.nav.pdl.forvalter.database;

import org.hibernate.dialect.PostgreSQL10Dialect;

import java.sql.Types;

public class PostgreSQLDialectExtended extends PostgreSQL10Dialect {

    public PostgreSQLDialectExtended() {
        this.registerColumnType(Types.JAVA_OBJECT, "jsonb");
    }
}
