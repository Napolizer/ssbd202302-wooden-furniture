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
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOK.9 - Edit own account")
class SelenideMOK9 {

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

//  @BeforeEach
//  public void setUp() {
//    ChromeOptions options = new ChromeOptions();
//    options.addArguments("start-maximized");
//    options.addArguments("--no-sandbox");
//    options.addArguments("--ignore-certificate-errors");
//    options.addArguments("--lang=en");
//    options.setExperimentalOption("prefs", Map.of("intl.accept_languages", "en"));
//    WebDriverRunner.setWebDriver(new ChromeDriver(options));
//    Configuration.baseUrl = "http://localhost:4200";
//  }
//
//  @AfterEach
//  public void tearDown() {
//    WebDriverRunner.closeWebDriver();
//  }

  @Test
  @DisplayName("Should properly log in and edit own account")
  void shouldProperlyLogInAndEditOwnAccount() {
   String newStreet = "Marszałkowska";
   String newStreetNumber = "100";
   String newCity = "Warszawa";
   String newPostalCode = "00-624";

    open("/");
    $(".title-text").shouldHave(text("Wooden Furniture"));
    $$("span").findBy(text("Login")).click();
    $$("input").findBy(attribute("data-placeholder", "login")).setValue("client1");
    $$("input").findBy(attribute("data-placeholder", "password")).setValue("Student123!");
    $(".mat-focus-indicator .login-button").click();
    $$(".mat-icon").filterBy(Condition.text("account_circle")).first().click();
    $$(".mat-menu-item").filterBy(Condition.text("My Account")).first().click();
    webdriver().shouldHave(urlContaining("/self"));
    $$(".mat-button").filterBy(Condition.text("Edit")).first().click();
    sleep(500);
    $$("input").findBy(attribute("formcontrolname", "street")).setValue(newStreet);
    $$("input").findBy(attribute("formcontrolname", "streetNumber")).setValue(newStreetNumber);
    $$("input").findBy(attribute("formcontrolname", "city")).setValue(newCity);
    $$("input").findBy(attribute("formcontrolname", "postalCode")).setValue(newPostalCode);
    $$(".mat-button").filterBy(Condition.text("Save")).first().click();
    $$(".mat-raised-button").filterBy(Condition.text("Confirm")).first().click();
    $$(".mat-icon").filterBy(Condition.text("refresh")).first().click();
    $$("small").filterBy(Condition.text("Street")).first().shouldHave(text(newStreet));
    $$("small").filterBy(Condition.text("City")).first().shouldHave(text(newCity));
    $$("small").filterBy(Condition.text("Street")).first().shouldHave(text(newStreet));
    $$("small").filterBy(Condition.text("Street number")).first().shouldHave(text(newStreetNumber));
    $$("small").filterBy(Condition.text("Postal code")).first().shouldHave(text(newPostalCode));
  }
}
