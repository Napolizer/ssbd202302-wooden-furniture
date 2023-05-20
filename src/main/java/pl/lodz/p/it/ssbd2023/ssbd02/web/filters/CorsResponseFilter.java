package pl.lodz.p.it.ssbd2023.ssbd02.web.filters;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class CorsResponseFilter implements ContainerResponseFilter {
  @Override
  public void filter(ContainerRequestContext requestContext,
                     ContainerResponseContext responseContext) throws IOException {
    responseContext.getHeaders().add(
        "Access-Control-Allow-Origin", "*");
    responseContext.getHeaders().add(
        "Access-Control-Allow-Credentials", "true");
    responseContext.getHeaders().add("Cache-Control", "no-cache");
    responseContext.getHeaders().add(
        "Access-Control-Allow-Headers",
        "origin, content-type, accept, authorization, if-match");
    responseContext.getHeaders().add(
        "Access-Control-Allow-Methods",
        "GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD");
  }
}
