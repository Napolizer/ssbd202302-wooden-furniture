package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.mapper;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.OrderedProduct;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Rate;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.OrderProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.OrderProductWithRateDto;

@Stateless
public class OrderProductMapper {

  @Inject
  private ProductMapper productMapper;

  public OrderProductDto mapToDto(OrderedProduct orderedProduct) {
    return OrderProductDto.builder()
      .amount(orderedProduct.getAmount())
      .productId(orderedProduct.getProduct().getId())
      .build();
  }


  public OrderProductWithRateDto mapToOrderProductWithRateDto(OrderedProduct orderProduct) {
    Integer rate = getRateFromOrderProduct(orderProduct);

    return OrderProductWithRateDto.builder()
            .amount(orderProduct.getAmount())
            .rate(rate)
            .oldRate(rate)
            .price(orderProduct.getPrice())
            .product(productMapper.mapToProductDto(orderProduct.getProduct()))
            .build();
  }

  private Integer getRateFromOrderProduct(OrderedProduct orderProduct) {
    Account account = orderProduct.getOrder().getAccount();

    return orderProduct.getProduct().getProductGroup().getRates().stream()
            .filter(rate -> rate.getAccount().getLogin().equals(account.getLogin()))
            .findFirst()
            .orElse(new Rate(0, account))
            .getValue();
  }
}
