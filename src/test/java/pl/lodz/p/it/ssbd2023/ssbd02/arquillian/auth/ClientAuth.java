package pl.lodz.p.it.ssbd2023.ssbd02.arquillian.auth;


import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;

import jakarta.annotation.security.RunAs;
import jakarta.ejb.Stateless;

@Stateless
@RunAs(CLIENT)
public class ClientAuth {
  public void call(Runnable runnable) {
    runnable.run();
  }
}
