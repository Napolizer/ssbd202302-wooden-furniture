package pl.lodz.p.it.ssbd2023.ssbd02.web.selenide;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.localStorage;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

class SelenideMOK19 {
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
  void shouldBlockAccountAfterThreeAttempts() {
    open("/");
    assertThat(localStorage().containsItem("token"), equalTo(false));
    assertThat(localStorage().containsItem("locale"), equalTo(false));
    assertThat(localStorage().containsItem("refreshToken"), equalTo(false));
    $(".title-text").shouldHave(text("Wooden Furniture"));
    $$("span").findBy(text("Login")).click();

    for (int i = 0; i < 3; i++) {
      failToAuthenticateWithWrongPassword();
    }

    $$("input").findBy(attribute("data-placeholder", "login")).setValue("client");
    $$("input").findBy(attribute("data-placeholder", "password")).setValue("WrongPassword#!123");
    $(".mat-focus-indicator .login-button").click();
    webdriver().shouldHave(urlContaining("/login"));

    $$("input").findBy(attribute("data-placeholder", "login")).setValue("administrator");
    $$("input").findBy(attribute("data-placeholder", "password")).setValue("Student123!");
    $(".mat-focus-indicator .login-button").click();

    $$(".mat-icon").filterBy(Condition.text("menu")).first().click();
    $$(".mat-menu-item").filterBy(Condition.text("Admin Panel")).first().click();
    webdriver().shouldHave(urlContaining("/admin"));
    $$(".mat-icon").filterBy(Condition.text("remove_red_eye")).get(4).click();
    $$("small").filterBy(Condition.text("Account state")).first().shouldHave(text("Blocked"));
    $$(".mat-button").filterBy(Condition.text("Activate")).first().click();
    $$(".mat-raised-button").filterBy(Condition.text("Confirm")).first().click();
    sleep(1000);
    $$("small").filterBy(Condition.text("Account state")).first().shouldHave(text("Active"));
  }

  private void failToAuthenticateWithWrongPassword() {
    $$("input").findBy(attribute("data-placeholder", "login")).setValue("client");
    $$("input").findBy(attribute("data-placeholder", "password")).setValue("WrongPassword#!123");
    $(".mat-focus-indicator .login-button").click();
    sleep(1000);
  }
}
