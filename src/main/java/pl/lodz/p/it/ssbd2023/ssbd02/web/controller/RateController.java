package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.security.Principal;
import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.rate.RateCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.rate.RateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api.RateEndpointOperations;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;

@Path("/rate")
public class RateController {

  @Inject
  private RateEndpointOperations rateEndpoint;
  @Inject
  private Principal principal;


  @DELETE
  @Path("id")
  @RolesAllowed(CLIENT)
  public Response delete(Long id) {
    rateEndpoint.delete(id, principal.getName());
    return Response.noContent().build();
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response create(RateCreateDto rate) {
    RateDto rateDto = rateEndpoint.create(principal.getName(), rate);
    return Response.ok(rateDto).build();
  }


  @PUT
  @Path("/archive")
  public Response archive(RateDto entity) {
    throw new UnsupportedOperationException();
  }

  @PUT
  @Path("/id/{id}")
  public Response update(@PathParam("id") Long id, RateDto entity) {
    throw new UnsupportedOperationException();
  }


  @GET
  @Path("/id/{id}")
  public Response find(@PathParam("id") Long id) {
    throw new UnsupportedOperationException();
  }

  @GET
  public Response findAll() {
    throw new UnsupportedOperationException();
  }

  @GET
  @Path("/present")
  public Response findAllPresent() {
    throw new UnsupportedOperationException();
  }


  @GET
  @Path("/archive")
  public List<RateDto> findAllArchived() {
    throw new UnsupportedOperationException();
  }


  @GET
  @Path("/value/{value}")
  public List<RateDto> findAllByValue(@PathParam("value") Integer value) {
    throw new UnsupportedOperationException();
  }

  @GET
  @Path("/person/id/{id}")
  public List<RateDto> findAllByPersonId(@PathParam("id") Long personId) {
    throw new UnsupportedOperationException();
  }
}
