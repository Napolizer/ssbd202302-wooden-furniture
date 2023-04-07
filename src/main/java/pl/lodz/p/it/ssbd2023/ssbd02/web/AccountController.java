package pl.lodz.p.it.ssbd2023.ssbd02.web;

import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.validation.Valid;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.EditPersonInfoDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.AccountService;

@Path("/account")
public class AccountController {
    @Inject
    private AccountService accountService;

    @PUT
    @Path("/id/{login}/editOwnAccount")
    @Produces(MediaType.APPLICATION_JSON)
    public Response editOwnAccount(@PathParam("login") String login, @Valid EditPersonInfoDto editPersonInfoDto) {
        var json = Json.createObjectBuilder();
        if (accountService.getAccountByLogin(login).isEmpty()) {
            json.add("error", "Account not found");
            return Response.status(404).entity(json.build()).build();
        }
        accountService.editAccountInfo(login, editPersonInfoDto);
        return Response.ok(editPersonInfoDto).build();
    }
}
