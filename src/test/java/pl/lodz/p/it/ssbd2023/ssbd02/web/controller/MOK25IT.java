package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AccountUtil;

//@MicroShedTest
//@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOK.25 - Automatically delete inactive accounts")
public class MOK25IT {
  @BeforeAll
  public static void setup() {
    RestAssured.baseURI = "http://localhost:8080/api/v1";
  }
  @Nested
  class Positive {
    @DisplayName("Should properly delete inactive accounts")
    @Test
    void shouldProperlyDeleteInactiveAccounts() throws InterruptedException {
      AccountUtil.registerUser("inactiveuser");
      AccountUtil.registerUser("anotherinactiveuser");
      int allAccounts = AccountUtil.countAccounts();
      TimeUnit.SECONDS.sleep(30);
      assertThat(AccountUtil.countAccounts(), is(equalTo(allAccounts - 2)));
    }
  }
}
