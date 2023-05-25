package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Category;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api.CategoryEndpointOperations;

@Path("/category")
public class CategoryController {

  @Inject
  private CategoryEndpointOperations categoryEndpoint;

  @GET
  @Path("/parent")
  public Response findAllByParentCategory(Category parentCategory) {
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
  @Path("/archived")
  public Response findAllArchived() {
    throw new UnsupportedOperationException();
  }
}
