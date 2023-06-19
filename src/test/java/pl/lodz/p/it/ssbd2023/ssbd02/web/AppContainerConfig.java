package pl.lodz.p.it.ssbd2023.ssbd02.web;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.Map;
import org.microshed.testing.SharedContainerConfiguration;
import org.microshed.testing.testcontainers.ApplicationContainer;
import org.microshed.testing.testcontainers.internal.ImageFromDockerfile;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

public class AppContainerConfig implements SharedContainerConfiguration {
  @Container
  public static GenericContainer<?> frontend = new GenericContainer(
      new ImageFromDockerfile()
          .withDockerfile(Paths.get("src/main/ssbd202302-frontend/Dockerfile")))
          .withNetworkAliases("frontend")
          .withExposedPorts(80);

  @Container
  public static BrowserWebDriverContainer<?> chrome = new BrowserWebDriverContainer<>(firefoxImage())
          .withCapabilities(new ChromeOptions()
              .addArguments("--headless")
              .addArguments("--ignore-certificate-errors")
              .addArguments("--lang=en")
              .setExperimentalOption("prefs", Map.of("intl.accept_languages", "en")))
          .dependsOn(frontend)
          .withNetworkAliases("chrome")
          .withNetwork(frontend.getNetwork());
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
      .withEnv("JAVA_TOOL_OPTIONS",
          "-javaagent:/jacoco-agent/org.jacoco.agent-runtime.jar=destfile=/jacoco-agent/jacoco-it.exec")
      .withReadinessPath("/api/v1/health")
      .dependsOn(postgres)
      .dependsOn(chrome)
      .withStartupTimeout(Duration.ofMinutes(2));

  public static DockerImageName firefoxImage() {
    return DockerImageName.parse("selenium/standalone-chrome");
  }
}
