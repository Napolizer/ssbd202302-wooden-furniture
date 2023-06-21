package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.EMPLOYEE;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.json.Json;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.security.Principal;
import java.util.List;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.EditProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.EditProductGroupDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.OrderProductWithRateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductCreateWithImageDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductGroupCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api.ProductEndpointOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api.ProductGroupEndpointOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.file.FileUtils;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.SimpleLoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.web.mappers.FormDataMapper;

@Path("/product")
@Interceptors({SimpleLoggerInterceptor.class})
@RequestScoped
public class ProductController {

  @Inject
  private ProductEndpointOperations productEndpoint;

  @Inject
  private ProductGroupEndpointOperations productGroupEndpoint;

  @Inject
  private Principal principal;

  @PATCH
  @Path("/archive/{productId}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed(EMPLOYEE)
  public Response archiveProduct(@PathParam("productId") Long productId) {
    productEndpoint.archive(productId);
    return Response.ok(
            Json.createObjectBuilder()
                    .add("message", "moz.product.archive.successful")
                    .build()
    ).build();
  }

  @POST
  @Path("/new-image")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @RolesAllowed(EMPLOYEE)
  public Response createProductWithNewImage(@FormDataParam("product") FormDataBodyPart product,
                         @FormDataParam("image") InputStream fileInputStream,
                         @FormDataParam("image") FormDataContentDisposition fileMetaData) {
    ProductCreateDto productCreateDto =
            FormDataMapper.mapFormDataBodyPartToProductCreateDto(product);
    byte[] image = FileUtils.readImageFromFileInputStream(fileInputStream, fileMetaData);
    return Response.status(Response.Status.CREATED).entity(
            productEndpoint.createProductWithNewImage(productCreateDto, image, fileMetaData.getFileName())).build();
  }

  @POST
  @Path("/existing-image")
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed(EMPLOYEE)
  public Response createProductWithExistingImage(@NotNull @Valid ProductCreateWithImageDto productCreateWithImageDto) {
    return Response.status(Response.Status.CREATED)
            .entity(productEndpoint.createProductWithExistingImage(productCreateWithImageDto)).build();
  }

  @POST
  @Path("/group")
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed(EMPLOYEE)
  public Response createProductGroup(@NotNull @Valid ProductGroupCreateDto productGroupCreateDto) {
    return Response.status(Response.Status.CREATED)
            .entity(productGroupEndpoint.create(productGroupCreateDto)).build();
  }

  @PUT
  @Path("/group/archive/id/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed(EMPLOYEE)
  public Response archiveProductGroup(@PathParam("id") Long id) {
    return Response.ok(productGroupEndpoint.archive(id)).build();
  }

  @PUT
  @Path("/group/activate/id/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed(EMPLOYEE)
  public Response activateProductGroup(@PathParam("id") Long id) {
    return Response.ok(productGroupEndpoint.activate(id)).build();
  }

  @PUT
  @Path("/group/id/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed(EMPLOYEE)
  public Response editProductGroupName(@PathParam("id") Long id,
                                       @NotNull @Valid EditProductGroupDto editProductGroupDto) {
    return Response.ok(productGroupEndpoint.editProductGroupName(id, editProductGroupDto)).build();
  }

  @PUT
  @Path("/editProduct/id/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed(EMPLOYEE)
  public Response editProduct(@PathParam("id") Long id,
                                     @NotNull @Valid EditProductDto editProductDto) {
    return Response.ok(productEndpoint.editProduct(id, editProductDto)).build();
  }

  @GET
  @Path("/group/products/id/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response findAllProductsByProductGroupId(@PathParam("id") Long id) {
    return Response.ok(productEndpoint.findAllByProductGroupId(id)).build();
  }

  @GET
  @Path("/category/id/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response findAllProductsByCategoryId(@PathParam("id") Long id) {
    return Response.ok(productEndpoint.findAllByCategoryId(id)).build();
  }

  @GET
  @Path("/id/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response find(@PathParam("id") Long id) {
    ProductDto product = productEndpoint.find(id);

    return Response.ok(product).build();
  }

  @GET
  @Path("/id/{id}/history")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed(EMPLOYEE)
  public Response findProductHistory(@PathParam("id") Long id) {
    return Response.ok(productEndpoint.findProductHistory(id)).build();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response findAll() {
    List<ProductDto> productDtoList = productEndpoint.findAll();
    return Response.ok(productDtoList).build();
  }

  @GET
  @Path("/search")
  @Produces(MediaType.APPLICATION_JSON)
  public Response findAllByProductGroupColorAndWoodType(
          @QueryParam("productGroupId") Long productGroupId,
          @QueryParam("color") String color,
          @QueryParam("woodType") String woodType) {
    return Response.ok(productEndpoint.findAllByProductGroupColorAndWoodType(productGroupId, color, woodType))
            .build();
  }

  @GET
  @Path("/group/id/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed(EMPLOYEE)
  public Response findGroup(@PathParam("id") Long id) {
    return Response.ok(productGroupEndpoint.find(id)).build();
  }

  @GET
  @Path("/group")
  @Produces(MediaType.APPLICATION_JSON)
  public Response findAllGroups() {
    return Response.ok(productGroupEndpoint.findAll()).build();
  }

  @GET
  @Path("/client")
  @RolesAllowed(CLIENT)
  @Produces(MediaType.APPLICATION_JSON)
  public Response findClientProducts() {
    List<OrderProductWithRateDto> clientProducts =
            productEndpoint.findAllProductsBelongingToAccount(principal.getName());
    return Response.ok(clientProducts).build();
  }
}
