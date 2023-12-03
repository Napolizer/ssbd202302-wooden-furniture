package pl.lodz.p.it.ssbd2023.ssbd02.config;

import jakarta.ejb.Stateless;

@Stateless
public class HealthConfig {
  boolean isHealthy = true;

  public boolean isHealthy() {
    return isHealthy;
  }

  public void setHealthy(boolean healthy) {
    isHealthy = healthy;
  }
}
