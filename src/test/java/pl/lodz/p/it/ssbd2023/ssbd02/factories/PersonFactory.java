package pl.lodz.p.it.ssbd2023.ssbd02.factories;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Person;

@Stateless
public class PersonFactory {
  @PersistenceContext(unitName = "ssbd02adminPU")
  private EntityManager em;
  @Inject
  private AddressFactory addressFactory;

  public Person create() throws Exception {
    Person person = Person.builder()
        .firstName("Jan")
        .lastName("Kowalski")
        .address(addressFactory.create())
        .build();
    em.persist(person);
    return person;
  }

  public void clean() throws Exception {
    em.createQuery("DELETE FROM Person").executeUpdate();
    addressFactory.clean();
  }
}
