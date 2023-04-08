package pl.lodz.p.it.ssbd2023.ssbd02.config;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.sql.Connection;

@DataSourceDefinition(
        name = "java:app/jdbc/ssbd02admin",
        className = "org.postgresql.ds.PGSimpleDataSource",
        user = "ssbd02admin",
        password = "dbadmin",
        serverName = "${ENV=DB_HOST:localhost}",
        portNumber = 5432,
        databaseName = "ssbd02",
        initialPoolSize = 1,
        minPoolSize = 0,
        maxPoolSize = 1,
        maxIdleTime = 10)

@DataSourceDefinition(
        name = "java:app/jdbc/ssbd02mok",
        className = "org.postgresql.ds.PGSimpleDataSource",
        user = "ssbd02mok",
        password = "dbmok",
        serverName = "${ENV=DB_HOST:localhost}",
        portNumber = 5432,
        databaseName = "ssbd02",
        transactional = true,
        isolationLevel = Connection.TRANSACTION_READ_COMMITTED)

@DataSourceDefinition(
        name = "java:app/jdbc/ssbd02moz",
        className = "org.postgresql.ds.PGSimpleDataSource",
        user = "ssbd02moz",
        password = "dbmoz",
        serverName = "${ENV=DB_HOST:localhost}",
        portNumber = 5432,
        databaseName = "ssbd02",
        transactional = true,
        isolationLevel = Connection.TRANSACTION_READ_COMMITTED)

@Stateless
public class DataSourceConfig {
    @PersistenceContext(unitName = "ssbd02adminPU")
    private EntityManager em;
}
