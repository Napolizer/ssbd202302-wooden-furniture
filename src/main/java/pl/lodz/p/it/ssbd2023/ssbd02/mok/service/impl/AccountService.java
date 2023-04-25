package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl;

import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.*;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.*;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.EditPersonInfoAsAdminDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.EditPersonInfoDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.AccountFacadeOperations;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
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

    public void addAccessLevelToAccount(Long accountId, AccessLevel accessLevel)
            throws AccessLevelAlreadyAssignedException, AccountNotFoundException {

        Account foundAccount = accountFacade.findById(accountId).orElseThrow(AccountNotFoundException::new);
        List<AccessLevel> accessLevels = foundAccount.getAccessLevels();

        for (AccessLevel item : accessLevels) {
            if (item.getClass() == accessLevel.getClass()) {
                throw new AccessLevelAlreadyAssignedException();
            }
        }

        accessLevels.add(accessLevel);
        foundAccount.setAccessLevels(accessLevels);
        accountFacade.update(foundAccount);
    }

    public void removeAccessLevelFromAccount(Long accountId, AccessLevel accessLevel)
            throws AccessLevelNotAssignedException, AccountNotFoundException {


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
        throw new AccessLevelNotAssignedException();
    }

    public void editAccountInfo(String login, EditPersonInfoDto editPersonInfoDto)
            throws AccountNotFoundException {

        Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);
        account.getPerson().setFirstName(editPersonInfoDto.getFirstName());
        account.getPerson().setLastName(editPersonInfoDto.getLastName());

        Address address = account.getPerson().getAddress();
        address.setCountry(editPersonInfoDto.getCountry());
        address.setCity(editPersonInfoDto.getCity());
        address.setStreet(editPersonInfoDto.getStreet());
        address.setPostalCode(editPersonInfoDto.getPostalCode());
        address.setStreetNumber(editPersonInfoDto.getStreetNumber());

        accountFacade.update(account);
    }

    public void editAccountInfoAsAdmin(String login, EditPersonInfoAsAdminDto editPersonInfoAsAdminDto)
            throws AccountNotFoundException {

        Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);
        account.setEmail(editPersonInfoAsAdminDto.getEmail());
        account.getPerson().setFirstName(editPersonInfoAsAdminDto.getFirstName());
        account.getPerson().setLastName(editPersonInfoAsAdminDto.getLastName());

        Address address = account.getPerson().getAddress();
        address.setCountry(editPersonInfoAsAdminDto.getCountry());
        address.setCity(editPersonInfoAsAdminDto.getCity());
        address.setStreet(editPersonInfoAsAdminDto.getStreet());
        address.setPostalCode(editPersonInfoAsAdminDto.getPostalCode());
        address.setStreetNumber(editPersonInfoAsAdminDto.getStreetNumber());

        accountFacade.update(account);
    }

    public void registerAccount(Account account) {
        account.setAccountState(AccountState.NOT_VERIFIED);
        account.getAccessLevels().add(new Client());
        account.setFailedLoginCounter(0);
        accountFacade.create(account);
    }

    public void createAccount(Account account) {
        account.setFailedLoginCounter(0);
        accountFacade.create(account);
    }

    public void checkIfAccountExists(Account account) throws Exception {
        if(accountFacade.findByLogin(account.getLogin()).isPresent())
            throw new LoginAlreadyExistsException();

        if(accountFacade.findByEmail(account.getEmail()).isPresent())
            throw new EmailAlreadyExistsException();
    }

    public void changePassword(String login, String newPassword) throws AccountNotFoundException {
        Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);

        if (!Objects.equals(account.getPassword(), newPassword)) {
            account.setPassword(newPassword);
            accountFacade.update(account);
        }
    }

    public void changePasswordAsAdmin(String login, String newPassword) throws AccountNotFoundException {
        Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);

        if (!Objects.equals(account.getPassword(), newPassword)) {
            account.setPassword(newPassword);
            accountFacade.update(account);
        }
    }

    public void blockAccount(Long id) throws Exception {
        Account account = accountFacade.findById(id).orElseThrow(AccountNotFoundException::new);

        if(!account.getAccountState().equals(AccountState.ACTIVE))
            throw new IllegalAccountStateChangeException();

        account.setAccountState(AccountState.BLOCKED);
        accountFacade.update(account);
    }

    public void activateAccount(Long id) throws Exception {
        Account account = accountFacade.findById(id).orElseThrow(AccountNotFoundException::new);
        AccountState state = account.getAccountState();

        if(state.equals(AccountState.ACTIVE) || state.equals(AccountState.INACTIVE))
            throw new IllegalAccountStateChangeException();

        account.setAccountState(AccountState.ACTIVE);
        accountFacade.update(account);
    }

    public void updateFailedLoginCounter(Account account) throws AccountNotFoundException {

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
