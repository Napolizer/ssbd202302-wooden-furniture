package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.config.HealthConfig;

@Path("/break")
public class BreakController {
  @Inject
  private HealthConfig healthConfig;

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public Response breakHealth() {
    healthConfig.setHealthy(!healthConfig.isHealthy());
    return Response.ok("Healthy: " + healthConfig.isHealthy()).build();
  }
}
