package pl.lodz.p.it.ssbd2023.ssbd02.config;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class DataSourceConfig {
  @PersistenceContext(unitName = "ssbd02adminPU")
  private EntityManager em;
}
