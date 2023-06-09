package pl.lodz.p.it.ssbd2023.ssbd02.entities;


import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Dimensions {
  private Integer width;
  private Integer height;
  private Integer depth;
}
