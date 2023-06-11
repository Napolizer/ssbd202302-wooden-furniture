package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState;

@Data
public class ChangeOrderStateDto {
  @NotNull
  @Valid
  private OrderState state;
  @NotBlank
  private String hash;
}
