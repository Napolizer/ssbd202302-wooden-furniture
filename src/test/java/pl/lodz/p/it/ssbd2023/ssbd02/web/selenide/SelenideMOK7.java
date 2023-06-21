package pl.lodz.p.it.ssbd2023.ssbd02.web.selenide;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;

import java.time.Duration;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOK.7 - Change own password")
class SelenideMOK7 {
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
  @DisplayName("Should properly log in, change password and then log in again with new password")
  void shouldProperlyLogInChangePasswordAndThenLogInAgainWithNewPassword() {
    String currentPassword = "Student123!";
    String newPassword = "NewPassword123!";

    open("/");
    $(".title-text").shouldHave(text("Wooden Furniture"));
    $$("span").findBy(text("Login")).click();
    $$("input").findBy(attribute("data-placeholder", "login")).setValue("client2");
    $$("input").findBy(attribute("data-placeholder", "password")).setValue("Student123!");
    $(".mat-focus-indicator .login-button").click();

    $$(".mat-icon").filterBy(Condition.text("account_circle")).first().click();
    $$(".mat-menu-item").filterBy(Condition.text("My Account")).first().click();
    webdriver().shouldHave(urlContaining("/self"));
    $$(".mat-button").filterBy(Condition.text("Change password")).first().click();
    $$("input").findBy(attribute("formcontrolname", "currentPassword")).setValue(currentPassword);
    $$("input").findBy(attribute("formcontrolname", "newPassword")).setValue(newPassword);
    $$("input").findBy(attribute("formcontrolname", "confirmPassword")).setValue(newPassword);
    $$(".mat-button").filterBy(Condition.text("Change")).get(2).click();
    $$(".mat-raised-button").filterBy(Condition.text("Confirm")).first().click();

    $$(".mat-icon").filterBy(Condition.text("account_circle")).first().click();
    $$(".mat-icon").filterBy(Condition.text("exit_to_app")).first().click();
    webdriver().shouldHave(urlContaining("/home"));

    $$("span").findBy(text("Login")).click();
    $$("input").findBy(attribute("data-placeholder", "login")).setValue("client2");
    $$("input").findBy(attribute("data-placeholder", "password")).setValue(newPassword);
    $(".mat-focus-indicator .login-button").click();
    webdriver().shouldHave(urlContaining("/home"));
  }
}
