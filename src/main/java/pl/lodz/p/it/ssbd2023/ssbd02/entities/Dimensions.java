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
  private double width;
  private double height;
  private double depth;
}
