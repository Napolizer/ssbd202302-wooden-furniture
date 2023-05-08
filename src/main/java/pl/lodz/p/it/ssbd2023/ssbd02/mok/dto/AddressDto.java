package pl.lodz.p.it.ssbd2023.ssbd02.mok.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation.Capitalized;
import pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation.PostalCode;

@Data
@Builder
public class AddressDto {
  @Capitalized
  private String country;
  @Capitalized
  private String city;
  @Capitalized
  private String street;
  @PostalCode
  private String postalCode;
  @NotNull
  @Positive(message = "Street number must be a positive integer")
  private Integer streetNumber;
}
