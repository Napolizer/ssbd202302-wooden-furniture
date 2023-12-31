package pl.lodz.p.it.ssbd2023.ssbd02.mok.security;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccessLevel;

@Data
@AllArgsConstructor
public class TokenClaims {
  private String login;
  private List<AccessLevel> accessLevels;
}
