package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.CategoryName;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {

  @NotNull
  private Long id;

  @NotNull
  private CategoryName name;

  @NotNull
  @Builder.Default
  private List<CategoryDto> subcategories = new ArrayList<>();

  @NotNull
  private CategoryName parentName;
}
