package pl.lodz.p.it.ssbd2023.ssbd02.web;

import java.time.Duration;
import org.microshed.testing.SharedContainerConfiguration;
import org.microshed.testing.testcontainers.ApplicationContainer;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

public class AppContainerConfig implements SharedContainerConfiguration {
  @Container
  public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.2")
      .withNetworkAliases("db")
      .withDatabaseName("ssbd02")
      .withUsername("ssbd02admin")
      .withPassword("dbadmin")
      .withFileSystemBind("Docker/docker-entrypoint-initdb.d", "/docker-entrypoint-initdb.d", BindMode.READ_ONLY);
  @Container
  public static ApplicationContainer container = new PayaraApplicationContainer()
      .withNetworkAliases("app")
      .withAppContextRoot("/api/v1")
      .withFileSystemBind("./target/jacoco-agent", "/jacoco-agent")
      .withEnv("DB_HOST", "db")
      .withEnv("MAIL_MAIL", System.getenv("MAIL_MAIL"))
      .withEnv("MAIL_PASSWORD", System.getenv("MAIL_PASSWORD"))
//      .withEnv("JAVA_TOOL_OPTIONS",
//          "-javaagent:/jacoco-agent/org.jacoco.agent-runtime.jar=destfile=/jacoco-agent/jacoco-it.exec")
      .withReadinessPath("/api/v1/health")
      .dependsOn(postgres)
      .withStartupTimeout(Duration.ofMinutes(5));
}
