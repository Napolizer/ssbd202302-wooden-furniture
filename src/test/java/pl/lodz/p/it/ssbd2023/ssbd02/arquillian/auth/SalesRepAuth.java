package pl.lodz.p.it.ssbd2023.ssbd02.arquillian.auth;


import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.SALES_REP;

import jakarta.annotation.security.RunAs;
import jakarta.ejb.Stateless;

@Stateless
@RunAs(SALES_REP)
public class SalesRepAuth {
  public void call(Runnable runnable) {
    runnable.run();
  }
}
