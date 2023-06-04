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
public class CreateOrderDto {
  @NotNull
  @Builder.Default
  private List<ProductDto> products = new ArrayList<>();

  @Capitalized
  private String firstName;

  @Capitalized
  private String lastName;

  @NotNull
  private AddressDto addressDto;

  @NotNull
  private AccountWithoutSensitiveDataDto account;
}
