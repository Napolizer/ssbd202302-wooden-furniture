package pl.lodz.p.it.ssbd2023.ssbd02.web.selenide;

import static com.codeborne.selenide.Selenide.open;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class SelenideMOK3 {
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
  @DisplayName("Should properly log in and block account")
  void shouldProperlyBlockAccount() {
    open("/");
//    $(".title-text").shouldHave(text("Wooden Furniture"));
//    $$("span").findBy(text("Login")).click();
//    $$("input").findBy(attribute("data-placeholder", "login")).setValue("administrator");
//    $$("input").findBy(attribute("data-placeholder", "password")).setValue("Student123!");
//    $(".mat-focus-indicator .login-button").click();
//    $$(".mat-icon").filterBy(Condition.text("menu")).first().click();
//    $$(".mat-menu-item").filterBy(Condition.text("Admin Panel")).first().click();
//    webdriver().shouldHave(urlContaining("/admin"));
//    $$(".mat-icon").filterBy(Condition.text("remove_red_eye")).get(1).click();
//    $$(".mat-button").filterBy(Condition.text("Block")).first().click();
//    $$(".mat-raised-button").filterBy(Condition.text("Confirm")).first().click();
//    $$("small").filterBy(Condition.text("Account state")).first().shouldHave(text("Blocked"));
//    $$(".mat-button").filterBy(Condition.text("Activate")).first().click();
//    $$(".mat-raised-button").filterBy(Condition.text("Confirm")).first().click();
//    $$("small").filterBy(Condition.text("Account state")).first().shouldHave(text("Active"));
  }
}
