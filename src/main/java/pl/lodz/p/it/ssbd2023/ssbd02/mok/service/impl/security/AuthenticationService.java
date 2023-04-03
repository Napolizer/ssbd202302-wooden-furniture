package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationException;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.AccountService;

@Stateless
public class AuthenticationService {
    @Inject
    private AccountService accountService;

    public String authenticate(String login, String password) throws AuthenticationException {
        Account account = accountService
                .getAccountByLogin(login)
                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

        return "token";
    }
}
