package pl.lodz.p.it.ssbd2023.ssbd02.web;

import org.microshed.testing.SharedContainerConfiguration;
import org.microshed.testing.testcontainers.ApplicationContainer;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.time.Duration;

public class AppContainerConfig implements SharedContainerConfiguration {
    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.2")
            .withNetworkAliases("testpostgres")
            .withDatabaseName("ssbd02")
            .withUsername("ssbd02admin")
            .withPassword("dbadmin")
            .withFileSystemBind("Docker/docker-entrypoint-initdb.d", "/docker-entrypoint-initdb.d", BindMode.READ_ONLY);

    @Container
    public static ApplicationContainer container = new ApplicationContainer()
            .withAppContextRoot("/ssbd02-0.0.7/api/v1")
            .withEnv("DB_HOST", "testpostgres")
            .withReadinessPath("/ssbd02-0.0.7/api/v1/health")
            .dependsOn(postgres)
            .withStartupTimeout(Duration.ofMinutes(2));
}
