package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.security.Principal;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.rate.RateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.rate.RateInputDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api.RateEndpointOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.SimpleLoggerInterceptor;

@Path("/rate")
@Interceptors({SimpleLoggerInterceptor.class})
@RequestScoped
public class RateController {

  @Inject
  private RateEndpointOperations rateEndpoint;
  @Inject
  private Principal principal;


  @DELETE
  @Path("/id/{productGroupId}")
  @RolesAllowed(CLIENT)
  public Response delete(@PathParam("productGroupId") Long productGroupId) {
    rateEndpoint.delete(principal.getName(), productGroupId);
    return Response.noContent().build();
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed(CLIENT)
  public Response create(@NotNull @Valid RateInputDto rate) {
    RateDto rateDto = rateEndpoint.create(principal.getName(), rate);
    return Response.ok(rateDto).build();
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed(CLIENT)
  public Response changeRate(@NotNull @Valid RateInputDto rate) {
    RateDto updatedRate = rateEndpoint.update(principal.getName(), rate);
    return Response.ok(updatedRate).build();
  }
}
