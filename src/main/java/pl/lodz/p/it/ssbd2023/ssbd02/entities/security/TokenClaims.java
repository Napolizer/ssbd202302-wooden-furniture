package pl.lodz.p.it.ssbd2023.ssbd02.entities.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccessLevel;

import java.util.List;

@Data
@AllArgsConstructor
public class TokenClaims {
    private String login;
    private List<AccessLevel> accessLevels;
}
