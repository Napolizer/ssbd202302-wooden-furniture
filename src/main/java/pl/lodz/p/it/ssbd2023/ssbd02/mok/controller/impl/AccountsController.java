package pl.lodz.p.it.ssbd2023.ssbd02.mok.controller.impl;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountWithoutSensitiveDataDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.AccountService;

import java.util.ArrayList;
import java.util.List;

@Path("/accounts")
public class AccountsController {
    @Inject
    private AccountService accountService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAccounts() {
        List<Account> accounts = accountService.getAccountList();
        List<AccountWithoutSensitiveDataDto> accountsDto = new ArrayList<>();
        for (Account account : accounts) {
            accountsDto.add(new AccountWithoutSensitiveDataDto(account));
        }
        return Response.ok(accountsDto).build();
    }
}
