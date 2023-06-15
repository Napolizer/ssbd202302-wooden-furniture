package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation.Hash;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelOrderDto {
  @NotNull
  private Long id;
  @NotNull
  @Hash
  private String hash;
}
