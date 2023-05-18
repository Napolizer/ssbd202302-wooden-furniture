package pl.lodz.p.it.ssbd2023.ssbd02.arquillian.auth;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.EMPLOYEE;

import jakarta.annotation.security.RunAs;
import jakarta.ejb.Stateless;

@Stateless
@RunAs(EMPLOYEE)
public class EmployeeAuth {
  public void call(Runnable runnable) {
    runnable.run();
  }
}
