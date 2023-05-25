package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.WoodType;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.CreateProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.UpdateProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api.ProductEndpointOperations;

@Path("/product")
public class ProductController {

  private final ProductEndpointOperations productEndpoint;

  @Inject
  public ProductController(ProductEndpointOperations productEndpoint) {
    this.productEndpoint = productEndpoint;
  }

  @POST
  public Response create(CreateProductDto entity) {
    throw new UnsupportedOperationException();
  }

  @PUT
  @Path("/archive/id/{id}")
  public Response archive(@PathParam("id") Long id, UpdateProductDto entity) {
    throw new UnsupportedOperationException();
  }

  @PUT
  @Path("/id/{id}")
  public Response update(@PathParam("id") Long id, UpdateProductDto entity) {
    throw new UnsupportedOperationException();
  }

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
  @Path("/woodType/{woodType}")
  public Response findAllByWoodType(@PathParam("woodType") WoodType woodType) {
    throw new UnsupportedOperationException();
  }

  @GET
  @Path("/color")
  public Response findAllByColor(Color color) {
    throw new UnsupportedOperationException();
  }

  @GET
  @Path("/available")
  public Response findAllAvailable() {
    throw new UnsupportedOperationException();
  }

  @GET
  @Path("/price")
  public Response findAllByPrice(@QueryParam("minPrice") Double minPrice,
                                 @QueryParam("maxPrice") Double maxPrice) {
    throw new UnsupportedOperationException();
  }
}
