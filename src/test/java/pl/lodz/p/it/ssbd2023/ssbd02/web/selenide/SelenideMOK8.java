package pl.lodz.p.it.ssbd2023.ssbd02.web.selenide;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;

import java.time.Duration;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
public class SelenideMOK8 {
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
  @DisplayName("Should properly create user and make him change his password")
  void shouldProperlyBlockAccount() {
    open("/");
    $(".title-text").shouldHave(text("Wooden Furniture"));
    $$("span").findBy(text("Login")).click();
    $$("input").findBy(attribute("data-placeholder", "login")).setValue("administrator");
    $$("input").findBy(attribute("data-placeholder", "password")).setValue("Student123!");
    $(".mat-focus-indicator .login-button").click();
    $$(".mat-icon").filterBy(Condition.text("menu")).first().click();
    $$(".mat-menu-item").filterBy(Condition.text("Admin Panel")).first().click();
    webdriver().shouldHave(urlContaining("/admin"));
    $$(".mat-stroked-button").filterBy(Condition.text("Create account")).first().click();
    $$("input").findBy(attribute("formcontrolname", "login")).setValue("Newlogin");
    $$("input").findBy(attribute("formcontrolname", "password")).setValue("Student123!");
    $$("input").findBy(attribute("formcontrolname", "confirmPassword")).setValue("Student123!");
    $$("input").findBy(attribute("formcontrolname", "email")).setValue("email@email.com");
    $$("input").findBy(attribute("formcontrolname", "firstName")).setValue("Newaccount");
    $$("input").findBy(attribute("formcontrolname", "lastName")).setValue("Newaccount");
    $$("input").findBy(attribute("formcontrolname", "country")).setValue("Poland");
    $$("input").findBy(attribute("formcontrolname", "city")).setValue("Warsaw");
    $$("input").findBy(attribute("formcontrolname", "street")).setValue("Złota");
    $$("input").findBy(attribute("formcontrolname", "streetNumber")).setValue("12");
    $$("input").findBy(attribute("formcontrolname", "postalCode")).setValue("12-123");
    $$("mat-select").findBy(attribute("formcontrolname", "timeZone")).click();
    $$(".mat-option-text").findBy(text("Europe/Warsaw")).click();
    $$("mat-select").findBy(attribute("formcontrolname", "accessLevel")).click();
    $$(".mat-option-text").findBy(text("Client")).click();
    $$(".mat-button").findBy(text("Create")).click();
    $$(".mat-raised-button").filterBy(Condition.text("Confirm")).first().click();
    $$(".mat-icon").filterBy(Condition.text("account_circle")).first().click();
    $$(".mat-icon").filterBy(Condition.text("account_circle")).first().click();
    $$(".mat-menu-item").filterBy(Condition.text("Logout")).first().click();
    webdriver().shouldHave(urlContaining("/home"));
    $$("span").findBy(text("Login")).click();
    $$("input").findBy(attribute("data-placeholder", "login")).setValue("Newlogin");
    $$("input").findBy(attribute("data-placeholder", "password")).setValue("Student123!");
    $(".mat-focus-indicator .login-button").click();
    $(".mat-dialog-title").shouldHave(text("Change password"));
  }
}
