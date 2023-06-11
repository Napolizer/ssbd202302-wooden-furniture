package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.OrderProductDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderDto {
  @NotEmpty
  @Size(min = 1)
  private List<@Valid OrderProductDto> products;

  @Valid
  private ShippingDataDto shippingData;
}
