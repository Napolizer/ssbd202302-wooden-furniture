package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation.Capitalized;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AddressDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingDataDto {
  @Capitalized
  private String recipientFirstName;

  @Capitalized
  private String recipientLastName;

  @Valid
  @NotNull
  private AddressDto address;
}
