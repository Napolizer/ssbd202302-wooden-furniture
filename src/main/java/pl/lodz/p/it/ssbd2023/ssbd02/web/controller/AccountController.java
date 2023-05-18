package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.ADMINISTRATOR;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.EMPLOYEE;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.SALES_REP;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.json.Json;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountState;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccountNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd02.interceptors.SimpleLoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountRegisterDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountWithoutSensitiveDataDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.ChangePasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.EditPersonInfoDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.GoogleAccountRegisterDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.SetEmailToSendPasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.UserCredentialsDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.mapper.AccountMapper;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.endpoint.AccountEndpoint;
import pl.lodz.p.it.ssbd2023.ssbd02.web.mappers.DtoToEntityMapper;

@Path("/account")
@Interceptors({SimpleLoggerInterceptor.class})
public class AccountController {
  @Inject
  private AccountEndpoint accountEndpoint;
  @Inject
  private AccountMapper accountMapper;
  @Inject
  private Principal principal;
  @Inject
  private HttpServletRequest servletRequest;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed(ADMINISTRATOR)
  public Response getAllAccounts() {
    List<Account> accounts = accountEndpoint.getAccountList();
    List<AccountWithoutSensitiveDataDto> accountsDto = new ArrayList<>();
    for (Account account : accounts) {
      accountsDto.add(accountMapper.mapToAccountWithoutSensitiveDataDto(account));
    }
    return Response.ok(accountsDto).build();
  }

  @GET
  @Path("/id/{accountId}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed(ADMINISTRATOR)
  public Response getAccountByAccountId(@PathParam("accountId") Long accountId) {
    Optional<Account> accountOptional = accountEndpoint.getAccountByAccountId(accountId);
    if (accountOptional.isEmpty()) {
      throw ApplicationExceptionFactory.createAccountNotFoundException();
    }
    AccountWithoutSensitiveDataDto account = accountMapper.mapToAccountWithoutSensitiveDataDto(accountOptional.get());
    return Response.ok(account).build();
  }

  @GET
  @Path("/login/{login}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed(ADMINISTRATOR)
  public Response getAccountByLogin(@PathParam("login") String login) {
    Optional<Account> accountOptional = accountEndpoint.getAccountByLogin(login);
    if (accountOptional.isEmpty()) {
      throw ApplicationExceptionFactory.createAccountNotFoundException();
    }
    AccountWithoutSensitiveDataDto account = accountMapper.mapToAccountWithoutSensitiveDataDto(accountOptional.get());
    return Response.ok(account).build();
  }

  @GET
  @Path("/self")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public Response getOwnAccountInformation() {
    if (principal.getName() == null) {
      return Response.status(403).build();
    }
    Optional<Account> accountOptional = accountEndpoint.getAccountByLogin(principal.getName());
    if (accountOptional.isEmpty()) {
      throw ApplicationExceptionFactory.createAccountNotFoundException();
    }
    AccountWithoutSensitiveDataDto account =
        accountMapper.mapToAccountWithoutSensitiveDataDto(accountOptional.get());
    return Response.ok(account).build();
  }

  @PUT
  @Path("/id/{accountId}/accessLevel/{accessLevel}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed(ADMINISTRATOR)
  public Response addAccessLevelToAccount(@PathParam("accountId") Long accountId,
                                          @PathParam("accessLevel") String accessLevel) {
    if (accountEndpoint.getAccountByAccountId(accountId).isEmpty()) {
      throw ApplicationExceptionFactory.createAccountNotFoundException();
    }

    if (!accountEndpoint.getAccountByAccountId(accountId).get().getAccountState()
        .equals(AccountState.ACTIVE)) {
      throw ApplicationExceptionFactory.createAccountNotActiveException();
    }

    AccessLevel newAccessLevel =
        DtoToEntityMapper.mapAccessLevelDtoToAccessLevel(new AccessLevelDto(accessLevel));
    accountEndpoint.addAccessLevelToAccount(accountId, newAccessLevel);
    AccountWithoutSensitiveDataDto account = accountMapper.mapToAccountWithoutSensitiveDataDto(
        accountEndpoint.getAccountByAccountId(accountId).get());
    return Response.ok(account).build();
  }

  @PUT
  @Path("/id/{accountId}/accessLevel/change")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed(ADMINISTRATOR)
  public Response changeAccessLevel(@PathParam("accountId") Long accountId,
                                    @NotNull @Valid AccessLevelDto accessLevel) {
    AccessLevel newAccessLevel =
            DtoToEntityMapper.mapAccessLevelDtoToAccessLevel(accessLevel);
    Account upadatedAccount = accountEndpoint.changeAccessLevel(accountId, newAccessLevel);
    AccountWithoutSensitiveDataDto account = accountMapper.mapToAccountWithoutSensitiveDataDto(upadatedAccount);

    return Response.ok(account).build();
  }

  @DELETE
  @Path("/id/{accountId}/accessLevel/{accessLevel}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed(ADMINISTRATOR)
  public Response removeAccessLevelFromAccount(@PathParam("accountId") Long accountId,
                                               @PathParam("accessLevel") String accessLevel) {
    if (accountEndpoint.getAccountByAccountId(accountId).isEmpty()) {
      throw ApplicationExceptionFactory.createAccountNotFoundException();
    }

    AccessLevel newAccessLevel =
        DtoToEntityMapper.mapAccessLevelDtoToAccessLevel(new AccessLevelDto(accessLevel));
    accountEndpoint.removeAccessLevelFromAccount(accountId, newAccessLevel);
    AccountWithoutSensitiveDataDto account = accountMapper.mapToAccountWithoutSensitiveDataDto(
        accountEndpoint.getAccountByAccountId(accountId).get());
    return Response.ok(account).build();
  }


  @POST
  @Path("/register")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response registerAccount(@NotNull @Valid AccountRegisterDto accountRegisterDto) {
    accountEndpoint.registerAccount(accountRegisterDto);
    return Response.status(Response.Status.CREATED).build();
  }

  @POST
  @Path("/create")
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed(ADMINISTRATOR)
  public Response createAccount(@NotNull @Valid AccountCreateDto accountCreateDto) {
    Account account = accountEndpoint.createAccount(accountCreateDto);
    return Response.status(Response.Status.CREATED)
            .entity(accountMapper.mapToAccountWithoutSensitiveDataDto(account)).build();
  }

  @PUT
  @Path("/self/changePassword")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public Response changePassword(@NotNull @Valid ChangePasswordDto changePasswordDto)
      throws AccountNotFoundException {

    String login = principal.getName();
    if (login == null || login.equals("ANONYMOUS")) {
      return Response.status(403).build();
    }
    Account account = accountEndpoint.changePassword(login, changePasswordDto.getPassword(),
            changePasswordDto.getCurrentPassword());
    AccountWithoutSensitiveDataDto changedAccount = accountMapper.mapToAccountWithoutSensitiveDataDto(account);

    return Response.ok(changedAccount).build();
  }

  @PUT
  @Path("/login/{login}/changePasswordAsAdmin")
  @Produces(MediaType.APPLICATION_JSON)
  public Response changePasswordAsAdmin(@PathParam("login") String login,
                                        @NotNull @Valid ChangePasswordDto changePasswordDto) {
    if (accountEndpoint.getAccountByLogin(login).isEmpty()) {
      throw ApplicationExceptionFactory.createAccountNotFoundException();
    }
    Account account = accountEndpoint.getAccountByLogin(login).get();
    if (Objects.equals(account.getPassword(), changePasswordDto.getPassword())) {
      throw ApplicationExceptionFactory.createOldPasswordGivenException();
    }
    accountEndpoint.changePasswordAsAdmin(login,
        changePasswordDto.getPassword());
    AccountWithoutSensitiveDataDto changedAccount = accountMapper.mapToAccountWithoutSensitiveDataDto(
        accountEndpoint.getAccountByLogin(login).get());
    return Response.ok(changedAccount).build();
  }

  @POST
  @Path("/login")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response login(@NotNull @Valid UserCredentialsDto userCredentialsDto,
                        @HeaderParam(HttpHeaders.ACCEPT_LANGUAGE) String locale) {
    var json = Json.createObjectBuilder();
    try {
      String token = accountEndpoint.login(userCredentialsDto, servletRequest.getRemoteAddr(), locale);
      json.add("token", token);
      return Response.ok(json.build()).build();
    } catch (AuthenticationException e) {
      json.add("message", e.getMessage());
      return Response.status(401).entity(json.build()).build();
    }
  }

  @PUT
  @Path("/login/{login}/editAccountAsAdmin")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed(ADMINISTRATOR)
  public Response editAccountAsAdmin(@PathParam("login") String login,
                                     @NotNull @Valid EditPersonInfoDto editPersonInfoDto) {
    if (accountEndpoint.getAccountByLogin(login).isEmpty()) {
      throw ApplicationExceptionFactory.createAccountNotFoundException();
    }
    accountEndpoint.editAccountInfoAsAdmin(login,
        editPersonInfoDto);
    return Response.ok(editPersonInfoDto).build();
  }

  @PATCH
  @Path("/block/{accountId}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed(ADMINISTRATOR)
  public Response blockAccount(@PathParam("accountId") Long accountId) {
    accountEndpoint.blockAccount(accountId);
    return Response.status(Response.Status.OK).build();
  }

  @PATCH
  @Path("/activate/{accountId}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed(ADMINISTRATOR)
  public Response activateAccount(@PathParam("accountId") Long accountId) {
    accountEndpoint.activateAccount(accountId);
    return Response.status(Response.Status.OK).build();
  }

  @PUT
  @Path("/login/{login}/editOwnAccount")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response editOwnAccount(@PathParam("login") String login,
                                 @NotNull @Valid EditPersonInfoDto editPersonInfoDto) {
    if (accountEndpoint.getAccountByLogin(login).isEmpty()) {
      throw ApplicationExceptionFactory.createAccountNotFoundException();
    }
    accountEndpoint.editAccountInfo(login, editPersonInfoDto);
    return Response.ok(editPersonInfoDto).build();
  }

  @PATCH
  @Path("/confirm")
  public Response confirmAccount(@QueryParam("token") String token) {
    accountEndpoint.confirmAccount(token);
    return Response.ok().build();
  }

  @POST
  @Path("/forgot-password")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response sendResetPasswordMail(@NotNull @Valid SetEmailToSendPasswordDto emailDto) {
    Optional<Account> foundAccount = accountEndpoint.getAccountByEmail(emailDto);
    if (foundAccount.isEmpty()) {
      throw ApplicationExceptionFactory.createEmailNotFoundException();
    }
    if (foundAccount.get().getAccountState() != AccountState.ACTIVE) {
      throw ApplicationExceptionFactory.createAccountNotActiveException();
    }
    accountEndpoint.sendResetPasswordEmail(emailDto);
    return Response.ok().build();
  }

  @GET
  @Path("/reset-password")
  public Response validatePasswordResetToken(@QueryParam("token") String token) {
    accountEndpoint.validateEmailToken(token, TokenType.PASSWORD_RESET);
    return Response.ok().build();
  }

  @PUT
  @Path("/reset-password")
  public Response resetPassword(@QueryParam("token") String token, @NotNull ChangePasswordDto changePasswordDto) {
    String login = accountEndpoint.validateEmailToken(token, TokenType.PASSWORD_RESET);
    accountEndpoint.resetPassword(login, changePasswordDto);
    return Response.ok().build();
  }

  @PUT
  @Path("/change-email/{accountId}")
  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public Response changeEmail(@PathParam("accountId") Long accountId,
                              @NotNull @Valid SetEmailToSendPasswordDto emailDto) {
    accountEndpoint.changeEmail(emailDto, accountId, principal.getName());
    return Response.ok().build();
  }

  @PATCH
  @Path("/change-email")
  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public Response submitEmail(@QueryParam("token") String token) {
    String login = accountEndpoint.validateEmailToken(token, TokenType.CHANGE_EMAIL);
    accountEndpoint.updateEmailAfterConfirmation(login);
    return Response.ok().build();
  }

  @GET
  @Path("/google/login")
  public Response getGoogleOauthLink() {
    return Response.ok().entity(Json.createObjectBuilder()
            .add("url", accountEndpoint.getGoogleOauthLink()).build()).build();
  }

  @POST
  @Path("/google/redirect")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response handleGoogleRedirect(@FormParam("code") String code,
                                       @FormParam("state") String state,
                                       @HeaderParam(HttpHeaders.ACCEPT_LANGUAGE) String locale) {
    return accountEndpoint.handleGoogleRedirect(code, state, servletRequest.getRemoteAddr(), locale);
  }

  @POST
  @Path("/google/register")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response registerGoogleAccount(@NotNull @Valid GoogleAccountRegisterDto googleAccountRegisterDto) {
    return accountEndpoint.registerGoogleAccount(googleAccountRegisterDto, servletRequest.getRemoteAddr());
  }
}
