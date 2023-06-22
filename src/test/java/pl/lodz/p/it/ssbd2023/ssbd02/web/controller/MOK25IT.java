package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AccountUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOK.25 - Automatically delete inactive accounts")
public class MOK25IT {
  @Nested
  class Positive {
    @DisplayName("Should properly delete inactive accounts")
    @Test
    void shouldProperlyDeleteInactiveAccounts() throws InterruptedException {
      TimeUnit.SECONDS.sleep(15);
      AccountUtil.registerUser("inactiveuser");
      AccountUtil.registerUser("anotherinactiveuser");
      int allAccounts = AccountUtil.countAccounts();
      TimeUnit.SECONDS.sleep(30);
      assertThat(AccountUtil.countAccounts(), is(equalTo(allAccounts - 2)));
    }
  }
}
