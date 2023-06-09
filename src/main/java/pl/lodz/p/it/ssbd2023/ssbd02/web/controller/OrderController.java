package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.EMPLOYEE;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
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
import java.security.Principal;
import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.CreateOrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.TimePeriodDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.UpdateOrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api.OrderEndpointOperations;


@Path("/order")
public class OrderController {

  @Inject
  private OrderEndpointOperations orderEndpoint;

  @Inject
  private Principal principal;

  @GET
  @Path("/account/login/{login}")
  public Response findByAccountLogin(@PathParam("login") String login) {
    throw new UnsupportedOperationException();
  }

  @GET
  @Path("/state/{state}")
  @RolesAllowed(EMPLOYEE)
  public Response findByState(@PathParam("state") OrderState orderState) {
    return Response.ok(orderEndpoint.findByState(orderState)).build();
  }

  @POST
  @Path("/create")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed(CLIENT)
  public Response create(@NotNull @Valid CreateOrderDto createOrderDto) {
    return Response.status(Response.Status.CREATED)
        .entity(orderEndpoint.create(createOrderDto)).build();
  }

  @PUT
  @Path("/archive/{id}")
  public Response archive(@PathParam("id") Long id) {
    throw new UnsupportedOperationException();
  }

  @PUT
  @Path("/id/{id}")
  public Response update(@PathParam("id") Long id, UpdateOrderDto entity) {
    throw new UnsupportedOperationException();
  }


  @GET
  @Path("/id/{id}")
  public Response find(@PathParam("id") Long id) {
    throw new UnsupportedOperationException();
  }

  @GET
  @RolesAllowed(EMPLOYEE)
  public Response findAll() {
    return Response.ok(orderEndpoint.findAll()).build();
  }


  @GET
  @Path("/present")
  @RolesAllowed(EMPLOYEE)
  public Response findAllPresent() {
    return Response.ok(orderEndpoint.findAllPresent()).build();
  }


  @GET
  @Path("/archived")
  @RolesAllowed(EMPLOYEE)
  public Response findAllArchived() {
    return Response.ok(orderEndpoint.findAllArchived()).build();
  }


  @PUT
  @Path("/cancel")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed(CLIENT)
  public Response cancelOrder(@NotNull @Valid OrderDto orderDto) {
    return Response.ok(orderEndpoint.cancelOrder(orderDto)).build();
  }

  @PUT
  @Path("/observe")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed(CLIENT)
  public Response observeOrder(@NotNull @Valid OrderDto orderDto) {
    return Response.ok(orderEndpoint.observeOrder(orderDto)).build();
  }


  @PUT
  @Path("/state/{id}")
  public Response changeStateOfOrder(@PathParam("id") Long id, OrderState state) {
    throw new UnsupportedOperationException();
  }

  @POST
  @Path("/report")
  public Response generateReport() {
    throw new UnsupportedOperationException();
  }

  @GET
  @Path("/filters")
  public Response findWithFilters(@QueryParam("price") Double orderPrice,
                                        @QueryParam("orderSize") Integer orderSize,
                                        @QueryParam("isCompany") boolean isCompany) {
    throw new UnsupportedOperationException();
  }

  @GET
  @Path("/statistics")
  public Response getStatistics(TimePeriodDto timePeriod) {
    throw new UnsupportedOperationException();
  }

  @GET
  @Path("/customer")
  @RolesAllowed(CLIENT)
  public Response findCustomerOrders() {
    List<OrderDto> clientOrders = orderEndpoint.findByAccountLogin(principal.getName());
    return Response.ok(clientOrders).build();
  }
}
