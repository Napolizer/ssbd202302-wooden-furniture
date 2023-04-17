package pl.lodz.p.it.ssbd2023.ssbd02.mok.endpoint;

import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Person;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountRegisterDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.AccountService;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security.PasswordHashService;
import pl.lodz.p.it.ssbd2023.ssbd02.web.mappers.DtoToEntityMapper;

import java.util.Optional;

@Stateful
@TransactionAttribute(TransactionAttributeType.NEVER)
public class AccountEndpoint {

    @Inject
    private AccountService accountService;
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
}
