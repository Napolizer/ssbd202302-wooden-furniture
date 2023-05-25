package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.OrderState;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.CreateOrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.TimePeriodDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.UpdateOrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api.OrderEndpointOperations;

import java.time.LocalDateTime;
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
  public Response create(CreateOrderDto entity) {
    throw new UnsupportedOperationException();
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
  @Path("/cancel/{id}")
  public Response cancelOrder(@PathParam("id") Long id) {
    throw new UnsupportedOperationException();
  }

  @PUT
  @Path("/observe/{id}")
  public Response observeOrder(@PathParam("id") Long id) {
    throw new UnsupportedOperationException();
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
}
