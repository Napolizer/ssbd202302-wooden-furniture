package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.*;
import pl.lodz.p.it.ssbd2023.ssbd02.mappers.DtoToEntityMapper;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountRegisterDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountWithoutSensitiveDataDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.AccountService;

import java.util.ArrayList;
import java.util.List;

@Path("/account")
public class AccountController {
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

    @GET
    @Path("/id/{accountId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccountByAccountId(@PathParam("accountId")Long accountId) {
        var json = Json.createObjectBuilder();
        if (accountService.getAccountById(accountId).isEmpty()) {
            json.add("error", "Account not found");
            return Response.status(404).entity(json.build()).build();
        }
        AccountWithoutSensitiveDataDto account = new AccountWithoutSensitiveDataDto(accountService.getAccountById(accountId).get());
        return Response.ok(account).build();
    }

    @GET
    @Path("/login/{login}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccountByLogin(@PathParam("login")String login) {
        var json = Json.createObjectBuilder();
        if (accountService.getAccountByLogin(login).isEmpty()) {
            json.add("error", "Account not found");
            return Response.status(404).entity(json.build()).build();
        }
        AccountWithoutSensitiveDataDto account = new AccountWithoutSensitiveDataDto(accountService.getAccountByLogin(login).get());
        return Response.ok(account).build();
    }

    @PUT
    @Path("/id/{accountId}/accessLevel/{accessLevel}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAccessLevelToAccount(@PathParam("accountId")Long accountId, @PathParam("accessLevel")String accessLevel) {
        var json = Json.createObjectBuilder();
        if (accountService.getAccountById(accountId).isEmpty()) {
            json.add("error", "Account not found");
            return Response.status(404).entity(json.build()).build();
        }

        AccessLevel newAccessLevel;
        switch (accessLevel) {
            case "Client" -> newAccessLevel = new Client();
            case "Administrator" -> newAccessLevel = new Administrator();
            case "Employee" -> newAccessLevel = new Employee();
            case "SalesRep" -> newAccessLevel = new SalesRep();
            default -> {
                json.add("error", "Given access level is invalid");
                return Response.status(400).entity(json.build()).build();
            }
        }

        accountService.addAccessLevelToAccount(accountId, newAccessLevel);
        AccountWithoutSensitiveDataDto account = new AccountWithoutSensitiveDataDto(accountService.getAccountById(accountId).get());
        return Response.ok(account).build();
    }

    @DELETE
    @Path("/id/{accountId}/accessLevel/{accessLevel}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeAccessLevelFromAccount(@PathParam("accountId")Long accountId, @PathParam("accessLevel")String accessLevel) {
        var json = Json.createObjectBuilder();
        if (accountService.getAccountById(accountId).isEmpty()) {
            json.add("error", "Account not found");
            return Response.status(404).entity(json.build()).build();
        }

        AccessLevel newAccessLevel;
        switch (accessLevel) {
            case "Client" -> newAccessLevel = new Client();
            case "Administrator" -> newAccessLevel = new Administrator();
            case "Employee" -> newAccessLevel = new Employee();
            case "SalesRep" -> newAccessLevel = new SalesRep();
            default -> {
                json.add("error", "Given access level is invalid");
                return Response.status(400).entity(json.build()).build();
            }
        }

        accountService.removeAccessLevelFromAccount(accountId, newAccessLevel);
        AccountWithoutSensitiveDataDto account = new AccountWithoutSensitiveDataDto(accountService.getAccountById(accountId).get());
        return Response.ok(account).build();
    }


    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerAccount(@Valid AccountRegisterDto accountRegisterDto) {
        accountService.registerAccount(DtoToEntityMapper.mapAccountRegisterDtoToPerson(accountRegisterDto));
        return Response.status(Response.Status.CREATED).build();
    }

}
