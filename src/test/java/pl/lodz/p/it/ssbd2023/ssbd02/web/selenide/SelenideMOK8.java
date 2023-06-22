package pl.lodz.p.it.ssbd2023.ssbd02.web.selenide;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class SelenideMOK8 {
  @BeforeAll
  public static void setUp() {
    ChromeOptions options = new ChromeOptions();
    options.addArguments("start-maximized");
    options.addArguments("--no-sandbox");
    options.addArguments("--ignore-certificate-errors");
    options.addArguments("--lang=en");
    options.setExperimentalOption("prefs", Map.of("intl.accept_languages", "en"));
    WebDriverRunner.setWebDriver(new ChromeDriver(options));
    Configuration.baseUrl = "http://localhost:4200";
  }

  @AfterEach
  public void tearDown() {
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
    $$(".mat-menu-item").filterBy(Condition.text("Logout")).first().click();
    webdriver().shouldHave(urlContaining("/home"));
    $$("span").findBy(text("Login")).click();
    $$("input").findBy(attribute("data-placeholder", "login")).setValue("Newlogin");
    $$("input").findBy(attribute("data-placeholder", "password")).setValue("Student123!");
    $(".mat-focus-indicator .login-button").click();
    $(".mat-dialog-title").shouldHave(text("Change password"));
  }
}
