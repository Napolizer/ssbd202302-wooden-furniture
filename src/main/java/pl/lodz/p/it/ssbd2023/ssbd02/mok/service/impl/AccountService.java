package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Person;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountState;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.EditPersonInfoDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.EditPersonInfoAsAdminDto;
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

    public void addAccessLevelToAccount(Long accountId, AccessLevel accessLevel) {
        Person foundPerson = personFacadeOperations.findByAccountId(accountId).orElseThrow();
        Account foundAccount = foundPerson.getAccount();
        List<AccessLevel> accessLevels = foundAccount.getAccessLevels();
        for (AccessLevel item : accessLevels) {
            if (item.getClass() == accessLevel.getClass()) {
                return;
            }
        }
        accessLevels.add(accessLevel);
        foundAccount.setAccessLevels(accessLevels);
        foundPerson.setAccount(foundAccount);
        personFacadeOperations.update(foundPerson);
    }

    public void removeAccessLevelFromAccount(Long accountId, AccessLevel accessLevel) {
        Person foundPerson = personFacadeOperations.findByAccountId(accountId).orElseThrow();
        Account foundAccount = foundPerson.getAccount();
        List<AccessLevel> accessLevels = foundAccount.getAccessLevels();
        for (AccessLevel item : accessLevels) {
            if (item.getClass() == accessLevel.getClass()) {
                accessLevels.remove(item);
                foundAccount.setAccessLevels(accessLevels);
                foundPerson.setAccount(foundAccount);
                personFacadeOperations.update(foundPerson);
                return;
            }
        }
    }

    public void editAccountInfo(String login, EditPersonInfoDto editPersonInfoDto) {
        Person person = personFacadeOperations.findByAccountLogin(login).orElse(null);
        person.setFirstName(editPersonInfoDto.getFirstName());
        person.setLastName(editPersonInfoDto.getLastName());
        Address address = new Address(editPersonInfoDto.getCountry(),editPersonInfoDto.getCity(),editPersonInfoDto.getStreet(),editPersonInfoDto.getPostalCode(), editPersonInfoDto.getStreetNumber());
        person.setAddress(address);
        personFacadeOperations.update(person);
    }

    public void editAccountInfoAsAdmin(String login, EditPersonInfoAsAdminDto editPersonInfoAsAdminDto) {
        Person person = personFacadeOperations.findByAccountLogin(login).orElse(null);
        person.setFirstName(editPersonInfoAsAdminDto.getFirstName());
        person.setLastName(editPersonInfoAsAdminDto.getLastName());
        Address address = new Address(editPersonInfoAsAdminDto.getCountry(),editPersonInfoAsAdminDto.getCity(),editPersonInfoAsAdminDto.getStreet(),editPersonInfoAsAdminDto.getPostalCode(), editPersonInfoAsAdminDto.getStreetNumber());
        person.setAddress(address);
        person.getAccount().setEmail(editPersonInfoAsAdminDto.getEmail());
        personFacadeOperations.update(person);
    }

    public void registerAccount(Person person) {
        //TODO password hash
        person.getAccount().setAccountState(AccountState.NOT_VERIFIED);
        person.getAccount().setFailedLoginCounter(0);
        person.getAccount().setArchive(false);

        personFacadeOperations.create(person);
        //TODO send confirmation mail
    }

}
