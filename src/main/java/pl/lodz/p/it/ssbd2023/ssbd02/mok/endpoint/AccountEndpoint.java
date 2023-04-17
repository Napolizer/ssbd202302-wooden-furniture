package pl.lodz.p.it.ssbd2023.ssbd02.mok.endpoint;

import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import jakarta.security.enterprise.AuthenticationException;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Person;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccessLevelNotAssignedException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccessLevelAlreadyAssignedException;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountRegisterDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.UserCredentialsDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.AccountService;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security.AuthenticationService;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security.PasswordHashService;
import pl.lodz.p.it.ssbd2023.ssbd02.web.mappers.DtoToEntityMapper;

import java.util.Optional;

@Stateful
@TransactionAttribute(TransactionAttributeType.NEVER)
public class AccountEndpoint {

    @Inject
    private AccountService accountService;
    @Inject
    private AuthenticationService authenticationService;
    @Inject
    private PasswordHashService passwordHashService;

    public void registerAccount(AccountRegisterDto accountRegisterDto) throws Exception {
        Person person = DtoToEntityMapper.mapAccountRegisterDtoToPerson(accountRegisterDto);
        accountService.checkIfPersonExists(person);
        person.getAccount().setPassword(passwordHashService.hashPassword(accountRegisterDto.getPassword()));
        accountService.registerAccount(person);

        //TODO confirmation email
    }

    public void createAccount(AccountCreateDto accountCreateDto) throws Exception {
        Person person = DtoToEntityMapper.mapAccountCreateDtoToPerson(accountCreateDto);
        accountService.checkIfPersonExists(person);
        person.getAccount().setPassword(passwordHashService.hashPassword(accountCreateDto.getPassword()));
        accountService.createAccount(person);
    }

    public void blockAccount(Long id) throws Exception {
        accountService.blockAccount(id);
        //TODO email message
    }

    public void activateAccount(Long id) throws Exception {
        accountService.activateAccount(id);
        //TODO email message
    }

    public Optional<Account> getAccountByAccountId(Long accountId) {
        return accountService.getAccountById(accountId);
    }

    public Optional<Account> getAccountByLogin(String login) {
        return accountService.getAccountByLogin(login);
    }

    public String login(UserCredentialsDto userCredentialsDto) throws AuthenticationException {
        return authenticationService.login(userCredentialsDto.getLogin(), userCredentialsDto.getPassword());
    }

    public void addAccessLevelToAccount(Long accountId, AccessLevel accessLevel) throws AccessLevelAlreadyAssignedException {
        accountService.addAccessLevelToAccount(accountId, accessLevel);
    }

    public void removeAccessLevelFromAccount(Long accountId, AccessLevel accessLevel) throws AccessLevelNotAssignedException {
        accountService.removeAccessLevelFromAccount(accountId, accessLevel);
    }

    public void changePassword(String login, String newPassword) {
        accountService.changePassword(login, newPassword);
    }

    public void changePasswordAsAdmin(String login, String newPassword) {
        accountService.changePasswordAsAdmin(login, newPassword);
    }
}
