package pl.lodz.p.it.ssbd2023.ssbd02.web.selenide;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.*;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;

import java.time.Duration;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
public class SelenideMOK4 {
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
  @DisplayName("Should properly add employee access level to account")
  void shouldProperlyAddEmployeeAccessLevelToAccount() {
    open("/");
    $(".title-text").shouldHave(text("Wooden Furniture"));
    $$("span").findBy(text("Login")).click();
    $$("input").findBy(attribute("data-placeholder", "login")).setValue("administrator");
    $$("input").findBy(attribute("data-placeholder", "password")).setValue("Student123!");
    $(".mat-focus-indicator .login-button").click();
    webdriver().shouldHave(urlContaining("/home"));

    $$(".mat-icon").filterBy(Condition.text("menu")).first().click();
    $$(".mat-menu-item").filterBy(Condition.text("Admin Panel")).first().click();
    webdriver().shouldHave(urlContaining("/admin"));
    $$(".mat-icon").filterBy(Condition.text("remove_red_eye")).get(1).click();
    $$(".mat-button").filterBy(Condition.text("Block")).first().click();
    $$(".mat-raised-button").filterBy(Condition.text("Confirm")).first().click();
    sleep(15000);
    $$("small").filterBy(Condition.text("Account state")).first().shouldHave(text("Blocked"));
    $$(".mat-button").filterBy(Condition.text("Activate")).first().click();
    $$(".mat-raised-button").filterBy(Condition.text("Confirm")).first().click();
    sleep(15000);
    $$("small").filterBy(Condition.text("Account state")).first().shouldHave(text("Active"));
  }
}
