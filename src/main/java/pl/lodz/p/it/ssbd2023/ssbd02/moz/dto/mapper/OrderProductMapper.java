package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.mapper;

import jakarta.ejb.Stateful;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.OrderedProduct;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.ProductGroup;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Rate;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.OrderProductWithRateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.OrderedProductDetailedDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.OrderedProductDto;

@Stateful
public class OrderProductMapper {

  @Inject
  private ProductMapper productMapper;

  public OrderedProductDto mapToDto(OrderedProduct orderedProduct) {
    return OrderedProductDto.builder()
        .amount(orderedProduct.getAmount())
        .productId(orderedProduct.getProduct().getId())
        .build();
  }

  public OrderedProductDetailedDto mapToDetailedDto(OrderedProduct orderedProduct) {
    return OrderedProductDetailedDto.builder()
        .amount(orderedProduct.getAmount())
        .price(orderedProduct.getPrice())
        .product(productMapper.mapToProductDto(orderedProduct.getProduct()))
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
    ProductGroup productGroup = orderProduct.getProduct().getProductGroup();

    return productGroup.getRates().stream()
            .filter(rate -> rate.getAccount().getLogin().equals(account.getLogin()))
            .findFirst()
            .orElse(new Rate(0, account, productGroup))
            .getValue();
  }
}
