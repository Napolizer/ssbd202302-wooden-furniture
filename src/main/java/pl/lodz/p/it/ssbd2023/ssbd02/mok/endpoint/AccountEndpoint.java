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
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccountNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountRegisterDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.EditPersonInfoAsAdminDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.EditPersonInfoDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.UserCredentialsDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.AccountService;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security.AuthenticationService;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.security.BCryptHashUtils;
import pl.lodz.p.it.ssbd2023.ssbd02.web.mappers.DtoToEntityMapper;

import java.util.List;
import java.util.Optional;

@Stateful
@TransactionAttribute(TransactionAttributeType.NEVER)
public class AccountEndpoint {

    @Inject
    private AccountService accountService;
    @Inject
    private AuthenticationService authenticationService;

    public void registerAccount(AccountRegisterDto accountRegisterDto) throws Exception {
        Account account = DtoToEntityMapper.mapAccountRegisterDtoToAccount(accountRegisterDto);
        accountService.checkIfAccountExists(account);
        account.setPassword(BCryptHashUtils.hashPassword(accountRegisterDto.getPassword()));
        accountService.registerAccount(account);

        //TODO confirmation email
    }

    public void createAccount(AccountCreateDto accountCreateDto) throws Exception {
        Account account = DtoToEntityMapper.mapAccountCreateDtoToAccount(accountCreateDto);
        accountService.checkIfAccountExists(account);
        account.setPassword(BCryptHashUtils.hashPassword(accountCreateDto.getPassword()));
        accountService.createAccount(account);
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

    public List<Account> getAccountList() {
        return accountService.getAccountList();
    }

    public String login(UserCredentialsDto userCredentialsDto) throws AuthenticationException {
        return authenticationService.login(userCredentialsDto.getLogin(), userCredentialsDto.getPassword());
    }

    public void addAccessLevelToAccount(Long accountId, AccessLevel accessLevel)
            throws AccessLevelAlreadyAssignedException, AccountNotFoundException {
        accountService.addAccessLevelToAccount(accountId, accessLevel);
    }

    public void removeAccessLevelFromAccount(Long accountId, AccessLevel accessLevel) throws AccessLevelNotAssignedException, AccountNotFoundException {
        accountService.removeAccessLevelFromAccount(accountId, accessLevel);
    }

    public void changePassword(String login, String newPassword) throws AccountNotFoundException {
        accountService.changePassword(login, newPassword);
    }

    public void changePasswordAsAdmin(String login, String newPassword) throws AccountNotFoundException {
        accountService.changePasswordAsAdmin(login, newPassword);
    }

    public void editAccountInfo(String login, EditPersonInfoDto editPersonInfoDto) throws AccountNotFoundException {
        accountService.editAccountInfo(login, DtoToEntityMapper.mapEditPersonInfoDtoToAccount(editPersonInfoDto));
    }

    public void editAccountInfoAsAdmin(String login, EditPersonInfoAsAdminDto editPersonInfoAsAdminDto)
            throws AccountNotFoundException {
        accountService.editAccountInfoAsAdmin(login,
                DtoToEntityMapper.mapEditPersonInfoAsAdminDtoToAccount(editPersonInfoAsAdminDto));
    }
}
