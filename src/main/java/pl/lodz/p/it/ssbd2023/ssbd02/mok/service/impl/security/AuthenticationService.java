package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationException;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountState;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.security.*;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.AccountService;

@Stateless
public class AuthenticationService {
    @Inject
    private AccountService accountService;
    @Inject
    private TokenService tokenService;

    public String login(String login, String password) throws AuthenticationException {
        Account account = accountService
                .getAccountByLogin(login)
                .orElseThrow(InvalidCredentialsException::new);

        if (account.getArchive()) {
            throw new AccountArchiveException();
        }

        if (account.getAccountState() == AccountState.BLOCKED) {
            throw new AccountBlockedException();
        }

        if (account.getAccountState() == AccountState.INACTIVE) {
            throw new AccountIsInactiveException();
        }

        if (!account.getPassword().equals(password)) {
            throw new InvalidCredentialsException();
        }

        return tokenService.generateToken(account);
    }
}
