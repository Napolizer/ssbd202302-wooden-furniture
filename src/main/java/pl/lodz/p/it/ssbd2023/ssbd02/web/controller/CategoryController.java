package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api.CategoryEndpointOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.SimpleLoggerInterceptor;

@Path("/category")
@Interceptors({SimpleLoggerInterceptor.class})
@RequestScoped
public class CategoryController {

  @Inject
  private CategoryEndpointOperations categoryEndpoint;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response findAllParentCategories() {
    return Response.ok(categoryEndpoint.findAllParentCategories()).build();
  }

}
