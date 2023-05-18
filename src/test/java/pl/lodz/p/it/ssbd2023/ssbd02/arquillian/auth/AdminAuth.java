package pl.lodz.p.it.ssbd2023.ssbd02.arquillian.auth;


import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.ADMINISTRATOR;

import jakarta.annotation.security.RunAs;
import jakarta.ejb.Stateless;

@Stateless
@RunAs(ADMINISTRATOR)
public class AdminAuth {
  public void call(Runnable runnable) {
    runnable.run();
  }
}
