package pl.lodz.p.it.ssbd2023.ssbd02.web;

import java.time.Duration;

import lombok.extern.java.Log;
import org.microshed.testing.SharedContainerConfiguration;
import org.microshed.testing.testcontainers.ApplicationContainer;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;

@Log
public class AppContainerConfig implements SharedContainerConfiguration {

  @Container
  public static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
      .withNetworkAliases("db")
      .withDatabaseName("ssbd02")
      .withUsername("ssbd02admin")
      .withPassword("dbadmin")
      .withFileSystemBind("Docker/docker-entrypoint-initdb.d", "/docker-entrypoint-initdb.d", BindMode.READ_ONLY);

  @Container
  public static ApplicationContainer container = new PayaraApplicationContainer()
      .withNetworkAliases("app")
      .withAppContextRoot("/api/v1")
      .withEnv("DB_HOST", "db")
      .withEnv("MAIL_MAIL", System.getenv("MAIL_MAIL"))
      .withEnv("MAIL_PASSWORD", System.getenv("MAIL_PASSWORD"))
      .withReadinessPath("/api/v1/health")
      .dependsOn(mysql)
      .withStartupTimeout(Duration.ofMinutes(5));
}
