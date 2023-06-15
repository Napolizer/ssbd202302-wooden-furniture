package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation.Capitalized;
import pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation.Login;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AddressDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.OrderedProductDetailedDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderWithProductsDto {
  @NotNull
  private Long id;

  @NotNull
  @Builder.Default
  private List<OrderedProductDetailedDto> orderedProducts = new ArrayList<>();

  private String orderState;

  @Capitalized
  private String recipientFirstName;

  @Capitalized
  private String recipientLastName;

  @NotNull
  private AddressDto recipientAddress;

  @Login
  private String accountLogin;

  @NotNull
  private String hash;

  @NotNull
  private Boolean observed;
  @NotNull
  @Positive
  private Double totalPrice;

  @Override
  public String toString() {
    return "OrderDto{}";
  }
}
