package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation.Hash;

@Data
public class ObserveOrderDto {
  @NotNull
  private Long id;
  @NotNull
  @Hash
  private String hash;
}
