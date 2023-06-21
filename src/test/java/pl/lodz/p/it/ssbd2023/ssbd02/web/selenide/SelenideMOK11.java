package pl.lodz.p.it.ssbd2023.ssbd02.web.selenide;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.localStorage;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOK.11 - Logout")
class SelenideMOK11 {
  public static BrowserWebDriverContainer<?> chrome = AppContainerConfig.chrome;

  @BeforeAll
  public static void setUp() {
    RemoteWebDriver driver = chrome.getWebDriver();
    WebDriverRunner.setWebDriver(driver);

    Configuration.timeout = Duration.ofSeconds(20).toMillis();
    Configuration.baseUrl = "http://frontend";
  }

  @AfterEach
  public void cleanUp() {
    localStorage().clear();
  }

  @Test
  @DisplayName("Should properly logout")
  void shouldProperlyLogout() {
    open("/");
    assertThat(localStorage().containsItem("token"), equalTo(false));
    assertThat(localStorage().containsItem("locale"), equalTo(false));
    assertThat(localStorage().containsItem("refreshToken"), equalTo(false));
    $(".title-text").shouldHave(text("Wooden Furniture"));
    $$("span").findBy(text("Login")).click();
    $$("input").findBy(attribute("data-placeholder", "login")).setValue("client");
    $$("input").findBy(attribute("data-placeholder", "password")).setValue("Student123!");
    $(".mat-focus-indicator .login-button").click();
    webdriver().shouldHave(urlContaining("/home"));
    assertThat(localStorage().containsItem("token"), equalTo(true));
    assertThat(localStorage().containsItem("refreshToken"), equalTo(true));
    $$(".mat-icon").filterBy(Condition.text("account_circle")).first().click();
    $$(".mat-menu-item").filterBy(Condition.text("Logout")).first().click();
    webdriver().shouldHave(urlContaining("/home"));
    assertThat(localStorage().containsItem("token"), equalTo(false));
    assertThat(localStorage().containsItem("refreshToken"), equalTo(false));

  }
}
