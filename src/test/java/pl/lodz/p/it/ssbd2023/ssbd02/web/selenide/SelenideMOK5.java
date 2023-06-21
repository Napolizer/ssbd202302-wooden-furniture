package pl.lodz.p.it.ssbd2023.ssbd02.web.selenide;

import com.codeborne.selenide.*;
import org.junit.jupiter.api.*;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

import java.time.Duration;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
public class SelenideMOK5 {
  public static BrowserWebDriverContainer<?> chrome = AppContainerConfig.chrome;

  @BeforeAll
  public static void setUp() {
    RemoteWebDriver driver = chrome.getWebDriver();
    WebDriverRunner.setWebDriver(driver);

    Configuration.timeout = Duration.ofSeconds(20).toMillis();
    Configuration.baseUrl = "http://frontend";
  }

  @AfterAll
  public static void tearDown() {
    WebDriverRunner.closeWebDriver();
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
    sleep(7000);
    $$(".mat-icon").filterBy(Condition.text("menu")).first().click();
    $$(".mat-menu-item").filterBy(Condition.text("admin_panel_settings")).first().click();
    sleep(4000);
    SelenideElement accountRow = $$(".mat-row").filterBy(text("client2")).first();
    accountRow.$(".action-menu-account").click();
    $(".mat-menu-item-role").hover();
    $(".add-role-button").click();
    $(".mat-select-role").click();
    $$(".mat-option-text").findBy(text("Employee")).click();
    $(".confirm-add-role-button").click();
    $$(".mat-raised-button").filterBy(Condition.text("Confirm")).last().click();
    sleep(10000);
    accountRow.$(".show").click();
    webdriver().shouldHave(urlContaining("/account"));
    $(By.tagName("body")).shouldHave(text("Client, Employee"));
    $(".remove-role-button").click();
    sleep(2000);
    $(".remove-role-employee").click();
    $$(".mat-raised-button").filterBy(Condition.text("Confirm")).last().click();
    sleep(5000);
    $(By.tagName("body")).shouldNotHave(text("Client, Employee"));
  }
}
