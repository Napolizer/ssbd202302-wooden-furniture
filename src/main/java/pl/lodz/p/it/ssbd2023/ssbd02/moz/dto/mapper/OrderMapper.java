package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.mapper;

import jakarta.ejb.Stateful;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Order;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.OrderProduct;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Person;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.mapper.AccountMapper;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.mapper.AddressMapper;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.CreateOrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.security.CryptHashUtils;

@Stateful
public class OrderMapper {
  @Inject
  private AddressMapper addressMapper;
  @Inject
  private AccountMapper accountMapper;
  @Inject
  private ProductMapper productMapper;

  public CreateOrderDto mapToCreateOrderDto(Order order) {
    List<ProductDto> productDtos = new ArrayList<>();
    order.getOrderedProducts().forEach(product -> productDtos.add(productMapper.mapToProductDto(product.getProduct())));
    return CreateOrderDto.builder()
        .products(productDtos)
        .firstName(order.getRecipient().getFirstName())
        .lastName(order.getRecipient().getLastName())
        .addressDto(addressMapper.mapToAddressDto(order.getDeliveryAddress()))
        .account(accountMapper.mapToAccountWithoutSensitiveDataDto(order.getAccount()))
        .observed(order.getObserved())
        .build();
  }

  public Order mapToOrder(CreateOrderDto createOrderDto) {
    Address address = addressMapper.mapToAddress(createOrderDto.getAddressDto());
    Person recipient = Person.builder()
        .firstName(createOrderDto.getFirstName())
        .lastName(createOrderDto.getLastName())
        .address(address)
        .build();

    Order order = Order.builder()
            .recipient(recipient)
            .deliveryAddress(address)
            .account(accountMapper.mapToAccount(createOrderDto.getAccount()))
            .observed(createOrderDto.getObserved())
            .totalPrice(0.0)
            .build();

    //FIXME create new OrderItemDto(productId, amount) instead of ProductDto and adjust this function
    createOrderDto.getProducts().forEach(productDto -> {
      order.getOrderedProducts().add(
              OrderProduct.builder().price(productDto.getPrice()).amount(productDto.getAmount())
                      .product(productMapper.mapToProduct(productDto))
                      .order(order).build()
      );
      order.setTotalPrice(order.getTotalPrice() + (productDto.getPrice() * productDto.getAmount()));
    });

    return order;
  }

  public OrderDto mapToOrderDto(Order order) {
    List<ProductDto> productDtos = new ArrayList<>();
    order.getOrderedProducts().forEach(product -> productDtos.add(productMapper.mapToProductDto(product.getProduct())));
    return OrderDto.builder()
        .id(order.getId())
        .products(productDtos)
        .recipientFirstName(order.getRecipient().getFirstName())
        .recipientLastName(order.getRecipient().getLastName())
        .recipientAddress(addressMapper.mapToAddressDto(order.getDeliveryAddress()))
        .account(accountMapper.mapToAccountWithoutSensitiveDataDto(order.getAccount()))
        .observed(order.getObserved())
        .hash(CryptHashUtils.hashVersion(order.getSumOfVersions()))
        .build();
  }
}
