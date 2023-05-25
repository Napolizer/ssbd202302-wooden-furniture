package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.ProductGroup;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.WoodType;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.CreateProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductGroupDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.UpdateProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api.ProductEndpointOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api.ProductGroupEndpointOperations;

@Path("/product")
public class ProductController {

  @Inject
  private ProductEndpointOperations productEndpoint;

  @Inject
  private ProductGroupEndpointOperations productGroupEndpoint;



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

  @POST
  @Path("/group")
  public Response createProductGroup(ProductGroupDto entity) {
    throw new UnsupportedOperationException();
  }

  @POST
  @Path("/group/archive/id/{id}")
  public Response archiveProductGroup(@PathParam("id") Long id) {
    throw new UnsupportedOperationException();
  }

  @PUT
  @Path("/group/id/{id}")
  public Response updateProductGroup(@PathParam("id") Long id, ProductGroup entity) {
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
  @Path("/group/id")
  public Response findGroup(Long id) {
    throw new UnsupportedOperationException();
  }

  @GET
  @Path("/group")
  public Response findAllGroups() {
    throw new UnsupportedOperationException();
  }

  @GET
  @Path("/group/present")
  public Response findAllPresentGroups() {
    throw new UnsupportedOperationException();
  }

  @GET
  @Path("/group/archived")
  public Response findAllGroupsArchived() {
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
