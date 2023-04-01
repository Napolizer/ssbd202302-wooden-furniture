package pl.lodz.p.it.ssbd2023.ssbd02.web;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.AccountService;

//@Path("/account") todo uncomment this
public class AccountController {
    @Inject
    private AccountService accountService;
}
