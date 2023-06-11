package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.mapper;

import jakarta.ejb.Stateless;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.OrderedProduct;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.OrderProductDto;

@Stateless
public class OrderProductMapper {

  public OrderProductDto mapToDto(OrderedProduct orderedProduct) {
    return OrderProductDto.builder()
      .amount(orderedProduct.getAmount())
      .productId(orderedProduct.getProduct().getId())
      .build();
  }
}
