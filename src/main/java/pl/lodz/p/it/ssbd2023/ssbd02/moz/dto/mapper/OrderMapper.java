package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.mapper;

import jakarta.ejb.Stateful;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Order;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Person;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Product;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.mapper.AccountMapper;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.mapper.AddressMapper;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.CreateOrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductDto;

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
    order.getProducts().forEach(product -> productDtos.add(productMapper.mapToProductDto(product)));
    return CreateOrderDto.builder()
        .products(productDtos)
        .firstName(order.getRecipient().getFirstName())
        .lastName(order.getRecipient().getLastName())
        .addressDto(addressMapper.mapToAddressDto(order.getDeliveryAddress()))
        .account(accountMapper.mapToAccountWithoutSensitiveDataDto(order.getAccount()))
        .build();
  }

  public Order mapToOrder(CreateOrderDto createOrderDto) {
    Address address = addressMapper.mapToAddress(createOrderDto.getAddressDto());
    Person recipient = Person.builder()
        .firstName(createOrderDto.getFirstName())
        .lastName(createOrderDto.getLastName())
        .address(address)
        .build();

    List<Product> products = new ArrayList<>();
    createOrderDto.getProducts().forEach(productDto -> products.add(productMapper.mapToProduct(productDto)));

    return Order.builder()
        .recipient(recipient)
        .deliveryAddress(address)
        .products(products)
        .account(accountMapper.mapToAccount(createOrderDto.getAccount()))
        .build();
  }
}
