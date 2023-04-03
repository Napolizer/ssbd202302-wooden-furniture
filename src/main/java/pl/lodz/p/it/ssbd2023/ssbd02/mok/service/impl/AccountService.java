package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Person;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.PersonFacadeOperations;

import java.util.List;
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

    public List<Account> getAccountList() {
        return personFacadeOperations.findAll()
                .stream()
                .map(Person::getAccount)
                .toList();
    }

    public void addNewAccessLevelToAccount(Long id, AccessLevel accessLevel) {
        Person foundPerson = personFacadeOperations.find(id).orElseThrow();
        Account foundAccount = foundPerson.getAccount();
        if (!foundAccount.getAccessLevels().contains(accessLevel)) {
            foundAccount.getAccessLevels().add(accessLevel);
            foundPerson.setAccount(foundAccount);
            personFacadeOperations.update(foundPerson);
        }
    }

    public void removeAccessLevelFromAccount(Long id, AccessLevel accessLevel) {
        Person foundPerson = personFacadeOperations.find(id).orElseThrow();
        Account foundAccount = foundPerson.getAccount();
        if (foundAccount.getAccessLevels().contains(accessLevel)) {
            foundAccount.getAccessLevels().remove(accessLevel);
            foundPerson.setAccount(foundAccount);
            personFacadeOperations.update(foundPerson);
        }
    }
}
