package pl.lodz.p.it.ssbd2023.ssbd02.mok.endpoint;

import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Person;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountRegisterDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.AccountService;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security.PasswordHashService;
import pl.lodz.p.it.ssbd2023.ssbd02.web.mappers.DtoToEntityMapper;

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
}
