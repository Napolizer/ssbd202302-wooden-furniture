package pl.lodz.p.it.ssbd2023.ssbd02.mok.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class GithubAccountInfoDto {
  private String login;
  private String email;
}
