package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.EMPLOYEE;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.SALES_REP;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.CancelOrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.ChangeOrderStateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.CreateOrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.ObserveOrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderDetailsDto;
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
  public Response create(@NotNull @Valid CreateOrderDto createOrderDto,
                         @Context SecurityContext securityContext) {
    String login = securityContext.getUserPrincipal().getName();
    return Response.status(Response.Status.CREATED)
        .entity(orderEndpoint.create(createOrderDto, login)).build();
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
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed(EMPLOYEE)
  public Response find(@PathParam("id") Long id) {
    return Response.ok(orderEndpoint.find(id)).build();
  }

  @GET
  @Path("/id/{id}/client")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed(CLIENT)
  public Response findAsClient(@PathParam("id") Long id) {
    return Response.ok(orderEndpoint.findAsClient(principal.getName(), id)).build();
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

  @GET
  @Path("/done")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed(SALES_REP)
  public Response findAllOrdersDone() {
    return Response.ok(orderEndpoint.findAllOrdersDone()).build();
  }

  @PUT
  @Path("/cancel")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed(CLIENT)
  public Response cancelOrder(@NotNull @Valid CancelOrderDto cancelOrderDto,
                              @Context SecurityContext securityContext) {
    String login = securityContext.getUserPrincipal().getName();
    return Response.ok(orderEndpoint.cancelOrder(cancelOrderDto, login)).build();
  }

  @PUT
  @Path("/employee/cancel")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed(EMPLOYEE)
  public Response cancelOrderAsEmployee(@NotNull @Valid CancelOrderDto cancelOrderDto) {
    return Response.ok(orderEndpoint.cancelOrderAsEmployee(cancelOrderDto.getId(), cancelOrderDto.getHash())).build();
  }

  @PUT
  @Path("/observe")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed(CLIENT)
  public Response observeOrder(@NotNull @Valid ObserveOrderDto observeOrderDto,
                               @Context SecurityContext securityContext) {
    String login = securityContext.getUserPrincipal().getName();
    return Response.ok(orderEndpoint.observeOrder(observeOrderDto.getId(), observeOrderDto.getHash(), login)).build();
  }


  @PUT
  @Path("/state/{id}")
  @RolesAllowed(EMPLOYEE)
  public Response changeOrderState(@PathParam("id") Long id, @NotNull @Valid ChangeOrderStateDto dto) {
    return Response.ok(orderEndpoint.changeOrderState(id, dto.getState(), dto.getHash())).build();
  }

  @GET
  @Path("/report")
  @Produces({"application/vnd.ms-excel", MediaType.APPLICATION_JSON})
  @RolesAllowed(SALES_REP)
  public Response generateReport(@QueryParam("startDate") String startDate,
                                 @QueryParam("endDate") String endDate,
                                 @HeaderParam(HttpHeaders.ACCEPT_LANGUAGE) String locale) {
    return Response.ok(orderEndpoint.generateReport(startDate, endDate, locale))
            .header("Content-Disposition", "attachment; filename=report.xlsx").build();
  }

  @GET
  @Path("/filters")
  @RolesAllowed(SALES_REP)
  public Response findWithFilters(@QueryParam("minPrice") Double minPrice,
                                        @QueryParam("maxPrice") Double maxPrice,
                                  @QueryParam("amount") Integer totalAmount,
                                  @QueryParam("company") boolean isCompany) {
    List<OrderDetailsDto> orderList = orderEndpoint.findWithFilters(minPrice, maxPrice, totalAmount, isCompany);
    return Response.ok(orderList).build();
  }

  @GET
  @Path("/statistics")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed(SALES_REP)
  public Response getStatistics(@QueryParam("startDate") String startDate,
                                @QueryParam("endDate") String endDate) {
    return Response.ok(orderEndpoint.findOrderStats(startDate, endDate)).build();
  }

  @GET
  @Path("/customer")
  @RolesAllowed(CLIENT)
  public Response findCustomerOrders() {
    List<OrderDto> clientOrders = orderEndpoint.findByAccountLogin(principal.getName());
    return Response.ok(clientOrders).build();
  }
}
