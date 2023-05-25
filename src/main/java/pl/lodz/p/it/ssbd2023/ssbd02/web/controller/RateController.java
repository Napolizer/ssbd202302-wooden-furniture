package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.rate.RateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api.RateEndpointOperations;

@Path("/rate")
public class RateController {

  @Inject
  private RateEndpointOperations rateEndpoint;


  @DELETE
  @Path("id")
  public void delete(Long id) {
    throw new UnsupportedOperationException();
  }

  @POST
  public Response create(RateDto entity) {
    throw new UnsupportedOperationException();
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
