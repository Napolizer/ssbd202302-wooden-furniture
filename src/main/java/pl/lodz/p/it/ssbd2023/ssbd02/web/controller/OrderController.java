package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;

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
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.CreateOrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.TimePeriodDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.UpdateOrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api.OrderEndpointOperations;

import java.util.List;


@Path("/order")
public class OrderController {

  @Inject
  private OrderEndpointOperations orderEndpoint;

  @GET
  @Path("/account/login/{login}")
  public Response findByAccountLogin(@PathParam("login") String login) {
    throw new UnsupportedOperationException();
  }

  @GET
  @Path("/state")
  public Response findByState(OrderState orderState) {
    throw new UnsupportedOperationException();
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
  @Path("/delivered/account/{id}")
  @RolesAllowed(CLIENT)
  public Response findDeliveredCustomerOrders(@PathParam("id") Long id) {
    List<OrderDto> clientOrders = orderEndpoint.findDeliveredCustomerOrders(id);
    return Response.ok(clientOrders).build();
  }
}
