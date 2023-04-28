package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountState;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccountNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.*;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.endpoint.AccountEndpoint;
import pl.lodz.p.it.ssbd2023.ssbd02.web.mappers.DtoToEntityMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Path("/account")
public class AccountController {
    @Inject
    private AccountEndpoint accountEndpoint;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMINISTRATOR")
    public Response getAllAccounts() {
        List<Account> accounts = accountEndpoint.getAccountList();
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
        Optional<Account> accountOptional = accountEndpoint.getAccountByAccountId(accountId);
        if (accountOptional.isEmpty()) {
            throw ApplicationExceptionFactory.createAccountNotFoundException();
        }
        AccountWithoutSensitiveDataDto account = new AccountWithoutSensitiveDataDto(accountOptional.get());
        return Response.ok(account).build();
    }

    @GET
    @Path("/login/{login}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMINISTRATOR")
    public Response getAccountByLogin(@PathParam("login")String login) {
        Optional<Account> accountOptional = accountEndpoint.getAccountByLogin(login);
        if (accountOptional.isEmpty()) {
            throw ApplicationExceptionFactory.createAccountNotFoundException();
        }
        AccountWithoutSensitiveDataDto account = new AccountWithoutSensitiveDataDto(accountOptional.get());
        return Response.ok(account).build();
    }

    @PUT
    @Path("/id/{accountId}/accessLevel/{accessLevel}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMINISTRATOR")
    public Response addAccessLevelToAccount(@PathParam("accountId")Long accountId, @PathParam("accessLevel")String accessLevel) {
        if (accountEndpoint.getAccountByAccountId(accountId).isEmpty()) {
            throw ApplicationExceptionFactory.createAccountNotFoundException();
        }

        if (!accountEndpoint.getAccountByAccountId(accountId).get().getAccountState().equals(AccountState.ACTIVE)) {
            throw ApplicationExceptionFactory.createAccountNotActiveException();
        }

        AccessLevel newAccessLevel = DtoToEntityMapper.mapAccessLevelDtoToAccessLevel(new AccessLevelDto(accessLevel));
        accountEndpoint.addAccessLevelToAccount(accountId, newAccessLevel);
        AccountWithoutSensitiveDataDto account = new AccountWithoutSensitiveDataDto(accountEndpoint.getAccountByAccountId(accountId).get());
        return Response.ok(account).build();
    }

    @DELETE
    @Path("/id/{accountId}/accessLevel/{accessLevel}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMINISTRATOR")
    public Response removeAccessLevelFromAccount(@PathParam("accountId")Long accountId, @PathParam("accessLevel")String accessLevel) {
        if (accountEndpoint.getAccountByAccountId(accountId).isEmpty()) {
            throw ApplicationExceptionFactory.createAccountNotFoundException();
        }

        AccessLevel newAccessLevel = DtoToEntityMapper.mapAccessLevelDtoToAccessLevel(new AccessLevelDto(accessLevel));
        accountEndpoint.removeAccessLevelFromAccount(accountId, newAccessLevel);
        AccountWithoutSensitiveDataDto account = new AccountWithoutSensitiveDataDto(accountEndpoint.getAccountByAccountId(accountId).get());
        return Response.ok(account).build();
    }


    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerAccount(@Valid AccountRegisterDto accountRegisterDto) {
        accountEndpoint.registerAccount(accountRegisterDto);
        return Response.status(Response.Status.CREATED).build();
    }

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMINISTRATOR")
    public Response createAccount(@Valid AccountCreateDto accountCreateDto) {
        accountEndpoint.createAccount(accountCreateDto);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/login/{login}/changePassword")
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePassword(@PathParam("login")String login, @Valid ChangePasswordDto changePasswordDto) throws AccountNotFoundException {
        if (accountEndpoint.getAccountByLogin(login).isEmpty()) {
            throw ApplicationExceptionFactory.createAccountNotFoundException();
        }
        Account account = accountEndpoint.getAccountByLogin(login).get();
        if (Objects.equals(account.getPassword(), changePasswordDto.getPassword())) {
            throw ApplicationExceptionFactory.createOldPasswordGivenException();
        }
        accountEndpoint.changePassword(login, changePasswordDto.getPassword()); //FIXME handle exception
        AccountWithoutSensitiveDataDto changedAccount = new AccountWithoutSensitiveDataDto(accountEndpoint.getAccountByLogin(login).get());
        return Response.ok(changedAccount).build();
    }

    @PUT
    @Path("/login/{login}/changePasswordAsAdmin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePasswordAsAdmin(@PathParam("login")String login, @Valid ChangePasswordDto changePasswordDto) {
        if (accountEndpoint.getAccountByLogin(login).isEmpty()) {
            throw ApplicationExceptionFactory.createAccountNotFoundException();
        }
        Account account = accountEndpoint.getAccountByLogin(login).get();
        if (Objects.equals(account.getPassword(), changePasswordDto.getPassword())) {
            throw ApplicationExceptionFactory.createOldPasswordGivenException();
        }
        accountEndpoint.changePasswordAsAdmin(login, changePasswordDto.getPassword()); //FIXME handle exception
        AccountWithoutSensitiveDataDto changedAccount = new AccountWithoutSensitiveDataDto(accountEndpoint.getAccountByLogin(login).get());
        return Response.ok(changedAccount).build();
    }

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(@NotNull @Valid UserCredentialsDto userCredentialsDto) {
        var json = Json.createObjectBuilder();
        try {
            String token = accountEndpoint.login(userCredentialsDto);
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
    public Response editAccountAsAdmin(@PathParam("login") String login, @Valid EditPersonInfoAsAdminDto editPersonInfoAsAdminDto){
        if (accountEndpoint.getAccountByLogin(login).isEmpty()) {
            throw ApplicationExceptionFactory.createAccountNotFoundException();
        }
        accountEndpoint.editAccountInfoAsAdmin(login, editPersonInfoAsAdminDto); //FIXME handle exception
        return Response.ok(editPersonInfoAsAdminDto).build();
    }

    @PATCH
    @Path("/block/{accountId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMINISTRATOR")
    public Response blockAccount(@PathParam("accountId")Long accountId) {
        accountEndpoint.blockAccount(accountId);
        return Response.status(Response.Status.OK).build();
    }

    @PATCH
    @Path("/activate/{accountId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMINISTRATOR")
    public Response activateAccount(@PathParam("accountId")Long accountId) {
        accountEndpoint.activateAccount(accountId);
        return Response.status(Response.Status.OK).build();
    }

    @PUT
    @Path("/login/{login}/editOwnAccount")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editOwnAccount(@PathParam("login") String login, @Valid EditPersonInfoDto editPersonInfoDto)  {
        if (accountEndpoint.getAccountByLogin(login).isEmpty()) {
            throw ApplicationExceptionFactory.createAccountNotFoundException();
        }
        accountEndpoint.editAccountInfo(login, editPersonInfoDto); //FIXME handle exception
        return Response.ok(editPersonInfoDto).build();
    }

//    @GET
//    @Path("/email/submit/{id}")
//    public Response submitEmail(@PathParam("id") Long accountId) {
//        try {
//            accountService.updateEmail(accountId);
//
//            return Response.ok().build();
//
//        } catch (Exception e) {
//            return Response.status(Response.Status.NOT_FOUND).entity("Not found").build();
//        }
//    }
}
