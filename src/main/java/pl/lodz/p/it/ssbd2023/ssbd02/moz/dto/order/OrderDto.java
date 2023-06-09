package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation.Capitalized;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountWithoutSensitiveDataDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AddressDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
  @NotNull
  private Long id;

  @NotNull
  @Builder.Default
  private List<ProductDto> products = new ArrayList<>();

  private String orderState;

  @Capitalized
  private String recipientFirstName;

  @Capitalized
  private String recipientLastName;

  @NotNull
  private AddressDto recipientAddress;

  @NotNull
  private AccountWithoutSensitiveDataDto account;

  @NotNull
  private String hash;

  @NotNull
  private Boolean observed;

  @Override
  public String toString() {
    return "OrderDto{}";
  }
}
