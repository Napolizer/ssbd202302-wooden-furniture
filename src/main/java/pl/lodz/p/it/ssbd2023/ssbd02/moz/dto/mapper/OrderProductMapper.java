package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.mapper;

import jakarta.ejb.Stateful;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.OrderedProduct;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.OrderedProductDto;

@Stateful
public class OrderProductMapper {

  public OrderedProductDto mapToDto(OrderedProduct orderedProduct) {
    return OrderedProductDto.builder()
      .amount(orderedProduct.getAmount())
      .productId(orderedProduct.getProduct().getId())
      .build();
  }
}
