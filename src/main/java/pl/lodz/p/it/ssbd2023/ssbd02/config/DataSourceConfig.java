package pl.lodz.p.it.ssbd2023.ssbd02.config;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@DataSourceDefinition(
        name = "java:app/jdbc/ssbd",
        className = "org.postgresql.ds.PGSimpleDataSource",
        user = "user",
        password = "password",
        serverName = "localhost",
        portNumber = 5432,
        databaseName = "db",
        initialPoolSize = 1,
        minPoolSize = 0,
        maxPoolSize = 1,
        maxIdleTime = 10
)
public class DataSourceConfig {
    @PersistenceContext(unitName = "ssbdPU")
    private EntityManager em;
}
