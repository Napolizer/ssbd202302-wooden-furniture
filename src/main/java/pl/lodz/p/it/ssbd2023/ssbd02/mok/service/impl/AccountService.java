package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Person;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.PersonFacadeOperations;

import java.util.Optional;

@Stateless
public class AccountService {
    @Inject
    private PersonFacadeOperations personFacadeOperations;

    public Optional<Account> getAccountByLogin(String login) {
        return personFacadeOperations.findByAccountLogin(login).map(Person::getAccount);
    }

    public Optional<Account> getAccountById(Long id) {
        return personFacadeOperations.find(id).map(Person::getAccount);
    }
}
