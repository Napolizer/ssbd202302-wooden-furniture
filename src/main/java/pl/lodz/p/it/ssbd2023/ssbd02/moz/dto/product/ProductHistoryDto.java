package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.ProductField;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductHistoryDto {
  @NotNull
  private ProductField fieldName;

  @NotNull
  private Double oldValue;

  @NotNull
  private Double newValue;

  @NotNull
  private LocalDateTime editDate;

  @NotNull
  private String editedBy;
}
