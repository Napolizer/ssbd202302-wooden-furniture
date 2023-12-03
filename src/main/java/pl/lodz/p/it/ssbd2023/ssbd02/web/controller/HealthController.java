package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.config.HealthConfig;

@Path("/health")
public class HealthController {
  @Inject
  private HealthConfig healthConfig;

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public Response getHealth() {
    if (!healthConfig.isHealthy()) {
      return Response.serverError().build();
    }
    return Response.ok("OK").build();
  }
}
