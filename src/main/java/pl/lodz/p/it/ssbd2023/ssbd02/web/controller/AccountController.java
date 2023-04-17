package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.*;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccessLevelAlreadyAssignedException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.InvalidAccessLevelException;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.*;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.endpoint.AccountEndpoint;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountRegisterDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountWithoutSensitiveDataDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.ChangePasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.UserCredentialsDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.AccountService;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security.AuthenticationService;

import java.util.*;

@Path("/account")
public class AccountController {
    @Inject
    private AccountService accountService;
    @Inject
    private AuthenticationService authenticationService;

    @Inject
    private AccountEndpoint accountEndpoint;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMINISTRATOR")
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
    @RolesAllowed("ADMINISTRATOR")
    public Response getAccountByAccountId(@PathParam("accountId")Long accountId) {
        JsonObjectBuilder json = Json.createObjectBuilder();
        Optional<Account> accountOptional = accountEndpoint.getAccountByAccountId(accountId);
        if (accountOptional.isEmpty()) {
            json.add("error", "Account not found");
            return Response.status(404).entity(json.build()).build();
        }
        AccountWithoutSensitiveDataDto account = new AccountWithoutSensitiveDataDto(accountOptional.get());
        return Response.ok(account).build();
    }

    @GET
    @Path("/login/{login}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMINISTRATOR")
    public Response getAccountByLogin(@PathParam("login")String login) {
        JsonObjectBuilder json = Json.createObjectBuilder();
        Optional<Account> accountOptional = accountEndpoint.getAccountByLogin(login);
        if (accountOptional.isEmpty()) {
            json.add("error", "Account not found");
            return Response.status(404).entity(json.build()).build();
        }
        AccountWithoutSensitiveDataDto account = new AccountWithoutSensitiveDataDto(accountOptional.get());
        return Response.ok(account).build();
    }

    @PUT
    @Path("/id/{accountId}/accessLevel/{accessLevel}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMINISTRATOR")
    public Response addAccessLevelToAccount(@PathParam("accountId")Long accountId, @PathParam("accessLevel")String accessLevel) throws AccessLevelAlreadyAssignedException {
        var json = Json.createObjectBuilder();
        if (accountService.getAccountById(accountId).isEmpty()) {
            json.add("error", "Account not found");
            return Response.status(404).entity(json.build()).build();
        }

        if (!accountService.getAccountById(accountId).get().getAccountState().equals(AccountState.ACTIVE)) {
            json.add("error", "Account is not active");
            return Response.status(400).entity(json.build()).build();
        }

        AccessLevel newAccessLevel;
        switch (accessLevel) {
            case "Client" -> newAccessLevel = new Client();
            case "Administrator" -> newAccessLevel = new Administrator();
            case "Employee" -> newAccessLevel = new Employee();
            case "SalesRep" -> newAccessLevel = new SalesRep();
            default -> {
                json.add("error", new InvalidAccessLevelException().getMessage());
                return Response.status(400).entity(json.build()).build();
            }
        }
        try {
            accountService.addAccessLevelToAccount(accountId, newAccessLevel);
            AccountWithoutSensitiveDataDto account = new AccountWithoutSensitiveDataDto(accountService.getAccountById(accountId).get());
            return Response.ok(account).build();
        } catch (Exception e) {
            json.add("error", e.getMessage());
            return Response.status(400).entity(json.build()).build();
        }
    }

    @DELETE
    @Path("/id/{accountId}/accessLevel/{accessLevel}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMINISTRATOR")
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
                json.add("error", new InvalidAccessLevelException().getMessage());
                return Response.status(400).entity(json.build()).build();
            }
        }
        try {
            accountService.removeAccessLevelFromAccount(accountId, newAccessLevel);
            AccountWithoutSensitiveDataDto account = new AccountWithoutSensitiveDataDto(accountService.getAccountById(accountId).get());
            return Response.ok(account).build();
        } catch (Exception e) {
            json.add("error", e.getMessage());
            return Response.status(400).entity(json.build()).build();
        }
    }


    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @DenyAll
    public Response registerAccount(@Valid AccountRegisterDto accountRegisterDto) {
        try {
            accountEndpoint.registerAccount(accountRegisterDto);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Json.createObjectBuilder()
                            .add("error", e.getMessage()).build()).build();
        }
        return Response.status(Response.Status.CREATED).build();
    }

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMINISTRATOR")
    public Response createAccount(@Valid AccountCreateDto accountCreateDto) {
        try {
            accountEndpoint.createAccount(accountCreateDto);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Json.createObjectBuilder()
                    .add("error", e.getMessage()).build()).build();
        }
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/login/{login}/changePassword")
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePassword(@PathParam("login")String login, @Valid ChangePasswordDto changePasswordDto) {
        var json = Json.createObjectBuilder();
        if (accountService.getAccountByLogin(login).isEmpty()) {
            json.add("error", "Account not found");
            return Response.status(404).entity(json.build()).build();
        }
        Account account = accountService.getAccountByLogin(login).get();
        if (Objects.equals(account.getPassword(), changePasswordDto.getPassword())) {
            json.add("error", "Given old password");
            return Response.status(400).entity(json.build()).build();
        }
        accountService.changePassword(login, changePasswordDto.getPassword());
        AccountWithoutSensitiveDataDto changedAccount = new AccountWithoutSensitiveDataDto(accountService.getAccountByLogin(login).get());
        return Response.ok(changedAccount).build();
    }

    @PUT
    @Path("/login/{login}/changePasswordAsAdmin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePasswordAsAdmin(@PathParam("login")String login, @Valid ChangePasswordDto changePasswordDto) {
        var json = Json.createObjectBuilder();
        if (accountService.getAccountByLogin(login).isEmpty()) {
            json.add("error", "Account not found");
            return Response.status(404).entity(json.build()).build();
        }
        Account account = accountService.getAccountByLogin(login).get();
        if (Objects.equals(account.getPassword(), changePasswordDto.getPassword())) {
            json.add("error", "Given old password");
            return Response.status(400).entity(json.build()).build();
        }
        accountService.changePasswordAsAdmin(login, changePasswordDto.getPassword());
        AccountWithoutSensitiveDataDto changedAccount = new AccountWithoutSensitiveDataDto(accountService.getAccountByLogin(login).get());
        return Response.ok(changedAccount).build();
    }

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(@NotNull @Valid UserCredentialsDto userCredentialsDto) {
        var json = Json.createObjectBuilder();
        try {
            String token = authenticationService.login(userCredentialsDto.getLogin(), userCredentialsDto.getPassword());
            json.add("token", token);
            return Response.ok(json.build()).build();
        } catch (AuthenticationException e) {
            json.add("error", e.getMessage());
            return Response.status(401).entity(json.build()).build();
        }
    }

    @PUT
    @Path("/login/{login}/editAccountAsAdmin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMINISTRATOR")
    public Response editAccountAsAdmin(@PathParam("login") String login, @Valid EditPersonInfoAsAdminDto editPersonInfoAsAdminDto) {
        var json = Json.createObjectBuilder();
        if (accountService.getAccountByLogin(login).isEmpty()) {
            json.add("error", "Account not found");
            return Response.status(404).entity(json.build()).build();
        }
        accountService.editAccountInfoAsAdmin(login, editPersonInfoAsAdminDto);
        return Response.ok(editPersonInfoAsAdminDto).build();
    }

    @PATCH
    @Path("/block/{accountId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMINISTRATOR")
    public Response blockAccount(@PathParam("accountId")Long accountId) {
        try {
            accountService.blockAccount(accountId);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Json.createObjectBuilder()
                            .add("error", e.getMessage()).build()).build();
        }
        return Response.status(Response.Status.OK).build();
    }

    @PATCH
    @Path("/activate/{accountId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMINISTRATOR")
    public Response activateAccount(@PathParam("accountId")Long accountId) {
        try {
            accountService.activateAccount(accountId);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Json.createObjectBuilder()
                    .add("error", e.getMessage()).build()).build();
        }
        return Response.status(Response.Status.OK).build();
    }

    @PUT
    @Path("/login/{login}/editOwnAccount")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editOwnAccount(@PathParam("login") String login, @Valid EditPersonInfoDto editPersonInfoDto) {
        var json = Json.createObjectBuilder();
        if (accountService.getAccountByLogin(login).isEmpty()) {
            json.add("error", "Account not found");
            return Response.status(404).entity(json.build()).build();
        }
        accountService.editAccountInfo(login, editPersonInfoDto);
        return Response.ok(editPersonInfoDto).build();
    }

    @GET
    @Path("/email/submit/{id}")
    public Response submitEmail(@PathParam("id") Long accountId) {
        try {
            accountService.updateEmail(accountId);

            return Response.ok().build();

        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).entity("Not found").build();
        }
    }
}
