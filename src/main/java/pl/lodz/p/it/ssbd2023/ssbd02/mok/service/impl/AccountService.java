package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl;

import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.*;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccountNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd02.interceptors.GenericServiceExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.AccountFacadeOperations;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors({
        GenericServiceExceptionsInterceptor.class
})
public class AccountService {
    @Inject
    private AccountFacadeOperations accountFacade;


    public Optional<Account> getAccountByLogin(String login) {
        return accountFacade.findByLogin(login);
    }

    public Optional<Account> getAccountById(Long id) {
        return accountFacade.findById(id);
    }

    public List<Account> getAccountList() {
        return accountFacade.findAll();
    }

    public void addAccessLevelToAccount(Long accountId, AccessLevel accessLevel) {
        Account foundAccount = accountFacade.findById(accountId).orElseThrow(AccountNotFoundException::new);
        List<AccessLevel> accessLevels = foundAccount.getAccessLevels();

        for (AccessLevel item : accessLevels) {
            if (item.getClass() == accessLevel.getClass()) {
                throw ApplicationExceptionFactory.createAccessLevelAlreadyAssignedException();
            }
        }
        accessLevel.setAccount(foundAccount);
        accessLevels.add(accessLevel);
        foundAccount.setAccessLevels(accessLevels);
        accountFacade.update(foundAccount);
    }

    public void removeAccessLevelFromAccount(Long accountId, AccessLevel accessLevel) {
        Account foundAccount = accountFacade.findById(accountId).orElseThrow(AccountNotFoundException::new);
        List<AccessLevel> accessLevels = foundAccount.getAccessLevels();

        for (AccessLevel item : accessLevels) {
            if (item.getClass() == accessLevel.getClass()) {
                accessLevels.remove(item);
                foundAccount.setAccessLevels(accessLevels);
                accountFacade.update(foundAccount);
                return;
            }
        }
        throw ApplicationExceptionFactory.createAccessLevelNotAssignedException();
    }

    public void editAccountInfo(String login, Account accountWithChanges) {
        Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);
        Person personWithChanges = accountWithChanges.getPerson();
        Address addressWithChanges = personWithChanges.getAddress();

        account.getPerson().setFirstName(personWithChanges.getFirstName());
        account.getPerson().setLastName(personWithChanges.getLastName());

        Address address = account.getPerson().getAddress();
        address.setCountry(addressWithChanges.getCountry());
        address.setCity(addressWithChanges.getCity());
        address.setStreet(addressWithChanges.getStreet());
        address.setPostalCode(addressWithChanges.getPostalCode());
        address.setStreetNumber(addressWithChanges.getStreetNumber());

        accountFacade.update(account);
    }

    public void editAccountInfoAsAdmin(String login, Account accountWithChanges) {

        Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);
        Person personWithChanges = accountWithChanges.getPerson();
        Address addressWithChanges = personWithChanges.getAddress();

        account.setEmail(accountWithChanges.getEmail());
        account.getPerson().setFirstName(personWithChanges.getFirstName());
        account.getPerson().setLastName(personWithChanges.getLastName());

        Address address = account.getPerson().getAddress();
        address.setCountry(addressWithChanges.getCountry());
        address.setCity(addressWithChanges.getCity());
        address.setStreet(addressWithChanges.getStreet());
        address.setPostalCode(addressWithChanges.getPostalCode());
        address.setStreetNumber(addressWithChanges.getStreetNumber());

        accountFacade.update(account);
    }

    public void registerAccount(Account account) {
        account.setAccountState(AccountState.NOT_VERIFIED);
        Client client = new Client();
        client.setAccount(account);
        account.getAccessLevels().add(client);
        account.setFailedLoginCounter(0);
        accountFacade.create(account);
    }

    public void createAccount(Account account) {
        account.setFailedLoginCounter(0);
        accountFacade.create(account);
    }

    public void changePassword(String login, String newPassword) {
        Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);

        if (!Objects.equals(account.getPassword(), newPassword)) {
            account.setPassword(newPassword);
            accountFacade.update(account);
        }
    }

    public void changePasswordAsAdmin(String login, String newPassword) {
        Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);

        if (!Objects.equals(account.getPassword(), newPassword)) {
            account.setPassword(newPassword);
            accountFacade.update(account);
        }
    }

    public void blockAccount(Long id) {
        Account account = accountFacade.findById(id).orElseThrow(AccountNotFoundException::new);

        if(!account.getAccountState().equals(AccountState.ACTIVE))
            throw ApplicationExceptionFactory.createIllegalAccountStateChangeException();

        account.setAccountState(AccountState.BLOCKED);
        accountFacade.update(account);
    }

    public void activateAccount(Long id) {
        Account account = accountFacade.findById(id).orElseThrow(AccountNotFoundException::new);
        AccountState state = account.getAccountState();

        if(state.equals(AccountState.ACTIVE) || state.equals(AccountState.INACTIVE))
            throw ApplicationExceptionFactory.createIllegalAccountStateChangeException();

        account.setAccountState(AccountState.ACTIVE);
        accountFacade.update(account);
    }

    public void updateFailedLoginCounter(Account account) {
        Account found = accountFacade.findById(account.getId()).orElseThrow(AccountNotFoundException::new);
        found.setFailedLoginCounter(account.getFailedLoginCounter());
        found.setAccountState(account.getAccountState());

        accountFacade.update(found);
    }
//    public Person updateEmail(Long accountId) {
//        Person person = personFacadeOperations.findByAccountId(accountId).orElseThrow();
//        Account account = person.getAccount();
//        account.setEmail(account.getNewEmail());
//        account.setNewEmail(null);
//        return personFacadeOperations.update(person);
//    }


}
