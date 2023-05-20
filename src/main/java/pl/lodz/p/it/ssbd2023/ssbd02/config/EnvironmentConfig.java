package pl.lodz.p.it.ssbd2023.ssbd02.config;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import pl.lodz.p.it.ssbd2023.ssbd02.config.enums.Environment;

@Stateless
public class EnvironmentConfig {
  private Environment environment;

  @PostConstruct
  private void init() {
    String env = System.getenv("ENVIRONMENT");
    if (env != null && env.trim().equalsIgnoreCase("PROD")) {
      environment = Environment.PROD;
    } else {
      environment = Environment.TEST;
    }
  }

  public Environment getEnvironment() {
    return environment;
  }

  public boolean isProd() {
    return environment == Environment.PROD;
  }

  public boolean isTest() {
    return environment == Environment.TEST;
  }
}
