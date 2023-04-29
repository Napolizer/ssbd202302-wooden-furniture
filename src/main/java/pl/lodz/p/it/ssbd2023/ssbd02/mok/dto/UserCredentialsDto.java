package pl.lodz.p.it.ssbd2023.ssbd02.mok.dto;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCredentialsDto implements Serializable {
  @NotBlank
  private String login;
  @NotBlank
  private String password;
}
