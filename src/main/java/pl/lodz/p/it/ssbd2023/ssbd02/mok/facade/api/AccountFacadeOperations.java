package pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api;

import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.facade.Facade;

import java.util.List;
import java.util.Optional;

public interface AccountFacadeOperations extends Facade<Account> {
    List<Account> findAllByFirstName(String firstName);
    List<Account> findAllByLastName(String lastName);
    List<Account> findAllByAddressId(Long addressId);
    Optional<Account> findByLogin(String login);
    Optional<Account> findByEmail(String email);
    Optional<Account> findById(Long accountId);
    void delete(Account account);
}