package pl.lodz.p.it.ssbd2023.ssbd02.factories;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Client;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.TimeZone;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.AccountState;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.AccountType;

@Stateless
public class AccountFactory {
  @PersistenceContext(unitName = "ssbd02adminPU")
  private EntityManager em;
  @Inject
  private PersonFactory personFactory;

  public Account createClient(String login) throws Exception {
    return create(login, List.of(new Client()));
  }

  public Account create(String login, List<AccessLevel> accessLevelList) throws Exception {
    Account account = Account.builder()
        .login(login)
        .password("password")
        .email("%s@ssbd.com".formatted(login))
        .person(personFactory.create())
        .locale("pl")
        .accountState(AccountState.ACTIVE)
        .accountType(AccountType.NORMAL)
        .accessLevels(accessLevelList)
        .timeZone(TimeZone.EUROPE_WARSAW)
        .build();
    for (AccessLevel accessLevel : accessLevelList) {
      accessLevel.setAccount(account);
    }
    em.persist(account);
    return account;
  }

  public void clean() throws Exception {
    em.createQuery("DELETE FROM access_level").executeUpdate();
    em.createQuery("DELETE FROM Account").executeUpdate();
    personFactory.clean();
  }
}
