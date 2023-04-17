package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl;

import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.*;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.*;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.EditPersonInfoAsAdminDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.EditPersonInfoDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.PersonFacadeOperations;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class AccountService {
    @Inject
    private PersonFacadeOperations personFacadeOperations;


    public Optional<Account> getAccountByLogin(String login) {
        return personFacadeOperations.findByAccountLogin(login).map(Person::getAccount);
    }

    public Optional<Account> getAccountById(Long id) {
        return personFacadeOperations.findByAccountId(id).map(Person::getAccount);
    }

    public List<Account> getAccountList() {
        return personFacadeOperations.findAll()
                .stream()
                .map(Person::getAccount)
                .toList();
    }

    public void addAccessLevelToAccount(Long accountId, AccessLevel accessLevel) throws AccessLevelAlreadyAssignedException {
        Person foundPerson = personFacadeOperations.findByAccountId(accountId).orElseThrow();
        Account foundAccount = foundPerson.getAccount();
        List<AccessLevel> accessLevels = foundAccount.getAccessLevels();
        for (AccessLevel item : accessLevels) {
            if (item.getClass() == accessLevel.getClass()) {
                throw new AccessLevelAlreadyAssignedException();
            }
        }
        accessLevels.add(accessLevel);
        foundAccount.setAccessLevels(accessLevels);
        foundPerson.setAccount(foundAccount);
        personFacadeOperations.update(foundPerson);
    }

    public void removeAccessLevelFromAccount(Long accountId, AccessLevel accessLevel) throws AccessLevelNotAssignedException {
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
        throw new AccessLevelNotAssignedException();
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
        person.getAccount().setAccountState(AccountState.NOT_VERIFIED);
        person.getAccount().getAccessLevels().add(new Client());
        person.getAccount().setFailedLoginCounter(0);
        personFacadeOperations.create(person);
    }

    public void createAccount(Person person) {
        person.getAccount().setFailedLoginCounter(0);
        personFacadeOperations.create(person);
    }

    public void checkIfPersonExists(Person person) throws Exception {
        if(personFacadeOperations.findByAccountLogin(person.getAccount().getLogin()).isPresent())
            throw new LoginAlreadyExistsException();

        if(personFacadeOperations.findByAccountEmail(person.getAccount().getEmail()).isPresent())
            throw new EmailAlreadyExistsException();
    }

    public void changePassword(String login, String newPassword) {
        Person person = personFacadeOperations.findByAccountLogin(login).orElseThrow();
        if (!Objects.equals(person.getAccount().getPassword(), newPassword)) {
            person.getAccount().setPassword(newPassword);
            personFacadeOperations.update(person);
        }
    }

    public void changePasswordAsAdmin(String login, String newPassword) {
        Person person = personFacadeOperations.findByAccountLogin(login).orElseThrow();
        if (!Objects.equals(person.getAccount().getPassword(), newPassword)) {
            person.getAccount().setPassword(newPassword);
            personFacadeOperations.update(person);
        }
    }

    public void blockAccount(Long id) throws Exception {
        Person person = personFacadeOperations.findByAccountId(id).orElseThrow(AccountNotFoundException::new);
        if(!person.getAccount().getAccountState().equals(AccountState.ACTIVE))
            throw new IllegalAccountStateChangeException();

        person.getAccount().setAccountState(AccountState.BLOCKED);
        personFacadeOperations.update(person);
    }

    public void activateAccount(Long id) throws Exception {
        Person person = personFacadeOperations.findByAccountId(id).orElseThrow(AccountNotFoundException::new);
        AccountState state = person.getAccount().getAccountState();
        if(state.equals(AccountState.ACTIVE) || state.equals(AccountState.INACTIVE))
            throw new IllegalAccountStateChangeException();

        person.getAccount().setAccountState(AccountState.ACTIVE);
        personFacadeOperations.update(person);
        //TODO email message
    }

    public void updateFailedLoginCounter(Account account) throws AccountNotFoundException {
        Person person = personFacadeOperations.findByAccountId(account.getId())
                .orElseThrow(AccountNotFoundException::new);
        person.getAccount().setFailedLoginCounter(account.getFailedLoginCounter());
        person.getAccount().setAccountState(account.getAccountState());

        personFacadeOperations.update(person);
    }
    public Person updateEmail(Long accountId) {
        Person person = personFacadeOperations.findByAccountId(accountId).orElseThrow();
        Account account = person.getAccount();
        account.setEmail(account.getNewEmail());
        account.setNewEmail(null);
        return personFacadeOperations.update(person);
    }


}
