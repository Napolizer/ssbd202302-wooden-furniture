package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/health")
public class HealthController {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getHealth() {
        return Response.ok("OK").build();
    }
}
