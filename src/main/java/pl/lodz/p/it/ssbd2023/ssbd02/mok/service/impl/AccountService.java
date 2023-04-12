package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.mail.MessagingException;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.*;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccessLevelAlreadyAssignedException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccountNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.IllegalAccountStateChangeException;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.EditPersonInfoAsAdminDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.EditPersonInfoDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.PersonFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security.PasswordHashService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Stateless
public class AccountService {
    @Inject
    private PersonFacadeOperations personFacadeOperations;

    @Inject
    private MailService mailService;

    @Inject
    private PasswordHashService passwordHashService;


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

    public void registerAccountAsGuest(Person person) throws MessagingException {
        person.getAccount().setPassword(passwordHashService.hashPassword(person.getAccount().getPassword()));
        person.getAccount().setAccountState(AccountState.NOT_VERIFIED);
        person.getAccount().setFailedLoginCounter(0);
        person.getAccount().setArchive(false);
        personFacadeOperations.create(person);

//        String locale = person.getAccount().getLocale();
//        mailService.sendMail(person.getAccount().getEmail(),
//                MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_CREATED_SUBJECT),
//                MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_CREATED_MESSAGE));
    }

    public void registerAccountAsAdmin(Person person) {
        person.getAccount().setPassword(passwordHashService.hashPassword(person.getAccount().getPassword()));
        person.getAccount().setFailedLoginCounter(0);
        person.getAccount().setArchive(false);

        personFacadeOperations.create(person);
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
        //TODO email message
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
}
