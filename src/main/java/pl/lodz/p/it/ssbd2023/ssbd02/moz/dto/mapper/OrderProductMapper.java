package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.mapper;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.OrderProduct;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.OrderProductDto;

@Stateless
public class OrderProductMapper {
  @Inject
  private ProductMapper productMapper;

  public OrderProductDto mapToDto(OrderProduct orderProduct) {
    return OrderProductDto.builder()
      .amount(orderProduct.getAmount())
      .price(orderProduct.getPrice())
      .product(productMapper.mapToProductDto(orderProduct.getProduct()))
      .build();
  }
}
