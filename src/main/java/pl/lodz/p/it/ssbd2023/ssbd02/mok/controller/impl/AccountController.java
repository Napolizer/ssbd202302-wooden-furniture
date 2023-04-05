package pl.lodz.p.it.ssbd2023.ssbd02.mok.controller.impl;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.*;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.AccountService;

@Path("/account")
public class AccountController {
    @Inject
    private AccountService accountService;

    @PUT
    @Path("/id/{accountId}/accessLevel/{accessLevel}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAccessLevelToAccount(@PathParam("accountId")Long accountId, @PathParam("accessLevel")String accessLevel) {
        if (accountService.getAccountById(accountId).isEmpty()) {
            return Response.status(404).build();
        }

        AccessLevel newAccessLevel;
        switch (accessLevel) {
            case "Client" -> newAccessLevel = new Client();
            case "Administrator" -> newAccessLevel = new Administrator();
            case "Employee" -> newAccessLevel = new Employee();
            case "SalesRep" -> newAccessLevel = new SalesRep();
            default -> {
                return Response.status(400).build();
            }
        }

        accountService.addAccessLevelToAccount(accountId, newAccessLevel);
        return Response.ok(newAccessLevel).build();
    }
}
