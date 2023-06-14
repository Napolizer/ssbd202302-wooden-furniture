package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.mapper;

import jakarta.ejb.Stateful;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Order;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.mapper.AddressMapper;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.CreateOrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderDetailsDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderWithProductsDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.ShippingDataDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.OrderedProductDetailedDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.OrderedProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.security.CryptHashUtils;

@Stateful
public class OrderMapper {
  @Inject
  private AddressMapper addressMapper;
  @Inject
  private OrderProductMapper orderProductMapper;
  @Inject
  private ProductMapper productMapper;

  public CreateOrderDto mapToCreateOrderDto(Order order) {
    List<OrderedProductDto> products = new ArrayList<>();
    order.getOrderedProducts().forEach(product -> products.add(orderProductMapper.mapToDto(product)));
    return CreateOrderDto.builder()
        .products(products)
        .shippingData(ShippingDataDto.builder()
            .recipientFirstName(order.getRecipientFirstName())
            .recipientLastName(order.getRecipientLastName())
            .recipientAddress(addressMapper.mapToAddressDto(order.getDeliveryAddress()))
            .build())
        .build();
  }

  public Order mapToOrder(CreateOrderDto createOrderDto) {
    Address address = addressMapper.mapToAddress(createOrderDto.getShippingData().getRecipientAddress());

    return Order.builder()
            .recipientFirstName(createOrderDto.getShippingData().getRecipientFirstName())
            .recipientLastName(createOrderDto.getShippingData().getRecipientLastName())
            .deliveryAddress(address)
            .totalPrice(0.0)
            .build();
  }

  public Order mapToOrderWithoutShippingData(CreateOrderDto createOrderDto) {
    return Order.builder()
        .totalPrice(0.0)
        .build();
  }

  public OrderDto mapToOrderDto(Order order) {
    List<OrderedProductDto> orderedProductDtoList = new ArrayList<>();
    order.getOrderedProducts().forEach(
        orderProduct -> orderedProductDtoList.add(orderProductMapper.mapToDto(orderProduct)));
    return OrderDto.builder()
        .id(order.getId())
        .orderedProducts(orderedProductDtoList)
        .orderState(order.getOrderState().name())
        .recipientFirstName(order.getRecipientFirstName())
        .recipientLastName(order.getRecipientLastName())
        .recipientAddress(addressMapper.mapToAddressDto(order.getDeliveryAddress()))
        .accountLogin(order.getAccount().getLogin())
        .observed(order.getObserved())
        .totalPrice(order.getTotalPrice())
        .hash(CryptHashUtils.hashVersion(order.getSumOfVersions()))
        .build();
  }

  public OrderDetailsDto mapToOrderDetailsDto(Order order) {
    List<OrderedProductDto> orderedProductDtoList = new ArrayList<>();
    order.getOrderedProducts().forEach(
        orderProduct -> orderedProductDtoList.add(orderProductMapper.mapToDto(orderProduct)));
    return OrderDetailsDto.builder()
        .id(order.getId())
        .orderedProducts(orderedProductDtoList)
        .orderState(order.getOrderState().name())
        .recipientFirstName(order.getRecipientFirstName())
        .recipientLastName(order.getRecipientLastName())
        .accountId(order.getAccount().getId())
        .accountLogin(order.getAccount().getLogin())
        .recipientAddress(addressMapper.mapToAddressDto(order.getDeliveryAddress()))
        .observed(order.getObserved())
        .totalPrice(order.getTotalPrice())
        .hash(CryptHashUtils.hashVersion(order.getSumOfVersions()))
        .build();
  }

  public OrderWithProductsDto mapToOrderWithProductsDto(Order order) {
    List<OrderedProductDetailedDto> orderedProductDtoList = new ArrayList<>();
    order.getOrderedProducts().forEach(
        orderProduct -> orderedProductDtoList.add(orderProductMapper.mapToDetailedDto(orderProduct)));
    return OrderWithProductsDto.builder()
        .id(order.getId())
        .orderedProducts(orderedProductDtoList)
        .orderState(order.getOrderState().name())
        .recipientFirstName(order.getRecipientFirstName())
        .recipientLastName(order.getRecipientLastName())
        .accountLogin(order.getAccount().getLogin())
        .recipientAddress(addressMapper.mapToAddressDto(order.getDeliveryAddress()))
        .observed(order.getObserved())
        .totalPrice(order.getTotalPrice())
        .hash(CryptHashUtils.hashVersion(order.getSumOfVersions()))
        .build();
  }
}
