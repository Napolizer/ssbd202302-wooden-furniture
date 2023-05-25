package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api.AddressEndpointOperations;

@Path("/address")
public class AddressController {

  @Inject
  private AddressEndpointOperations addressEndpoint;

  @GET
  @Path("/id")
  public Response find(Long id) {
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
  @Path("/archived")
  public Response findAllArchived() {
    throw new UnsupportedOperationException();
  }

  @GET
  @Path("/country/{country}")
  public Response findAllByCountry(@PathParam("country") String country) {
    throw new UnsupportedOperationException();
  }

  @GET
  @Path("/city/{city}")
  public Response findAllByCity(@PathParam("city") String city) {
    throw new UnsupportedOperationException();
  }

  @GET
  @Path("/street/{street}")
  public Response findAllByStreet(@PathParam("street") String street) {
    throw new UnsupportedOperationException();
  }

  @GET
  @Path("/postalCode/{postalCode}")
  public Response findAllByPostalCode(@PathParam("postalCode") String postalCode) {
    throw new UnsupportedOperationException();
  }

  @GET
  @Path("/streetNumber/{streetNumber}")
  public Response findAllByStreetNumber(@PathParam("streetNumber") Integer streetNumber) {
    throw new UnsupportedOperationException();
  }
}
