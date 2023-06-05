package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.EMPLOYEE;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.ProductGroup;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.WoodType;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductGroupCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.UpdateProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api.ProductEndpointOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api.ProductGroupEndpointOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.file.FileUtils;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.SimpleLoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.web.mappers.FormDataMapper;

@Path("/product")
@Interceptors({SimpleLoggerInterceptor.class})
public class ProductController {

  @Inject
  private ProductEndpointOperations productEndpoint;

  @Inject
  private ProductGroupEndpointOperations productGroupEndpoint;


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
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @RolesAllowed(EMPLOYEE)
  public Response createProduct(@FormDataParam("product") FormDataBodyPart product,
                         @FormDataParam("image") InputStream fileInputStream,
                         @FormDataParam("image") FormDataContentDisposition fileMetaData) {
    ProductCreateDto productCreateDto =
            FormDataMapper.mapFormDataBodyPartToProductCreateDto(product);
    byte[] image = FileUtils.readImageFromFileInputStream(fileInputStream, fileMetaData);
    String fileName = fileMetaData.getFileName();
    String fileFormat = "." + fileName.substring(fileName.lastIndexOf('.') + 1);
    return Response.status(Response.Status.CREATED)
            .entity(productEndpoint.create(productCreateDto, image, fileFormat)).build();
  }

  @POST
  @Path("/group")
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed(EMPLOYEE)
  public Response createProductGroup(@NotNull @Valid ProductGroupCreateDto productGroupCreateDto) {
    return Response.status(Response.Status.CREATED)
            .entity(productGroupEndpoint.create(productGroupCreateDto)).build();
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
  @Path("/id/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response find(@PathParam("id") Long id) {
    ProductDto product = productEndpoint.find(id);

    return Response.ok(product).build();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response findAll() {
    List<ProductDto> productDtoList = productEndpoint.findAll();
    return Response.ok(productDtoList).build();
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
