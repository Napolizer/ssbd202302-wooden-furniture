package pl.lodz.p.it.ssbd2023.ssbd02.factories;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Address;

@Stateless
public class AddressFactory {
  @PersistenceContext(unitName = "ssbd02adminPU")
  private EntityManager em;

  public Address create() throws Exception {
    Address address = Address.builder()
        .country("Polska")
        .city("Lodz")
        .street("Przybyszewskiego")
        .postalCode("22-22222")
        .streetNumber(12)
        .build();
    em.persist(address);
    return address;
  }

  public void clean() throws Exception {
    em.createQuery("DELETE FROM Address").executeUpdate();
  }
}
