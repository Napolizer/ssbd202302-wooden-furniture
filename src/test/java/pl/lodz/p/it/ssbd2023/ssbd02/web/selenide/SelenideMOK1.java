package pl.lodz.p.it.ssbd2023.ssbd02.web.selenide;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

class SelenideMOK1 {
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

  @AfterAll
  public static void tearDown() {
    WebDriverRunner.closeWebDriver();
  }

  @Test
  @DisplayName("Should properly register account")
  void shouldProperlyRegisterUser() {
    open("/");
    $(".title-text").shouldHave(text("Wooden Furniture"));
    $$("span").findBy(text("Sign up")).click();
    $(".form-title").shouldHave(text("Create your account"));
    $$("input").findBy(attribute("formcontrolname", "email")).setValue("selenide@ssbd.com");
    $$("input").findBy(attribute("formcontrolname", "login")).setValue("selenide");
    $$("input").findBy(attribute("formcontrolname", "password")).setValue("Selenide123!");
    $$("input").findBy(attribute("formcontrolname", "confirmPassword")).setValue("Selenide123!");
    $$(".mat-button-wrapper").findBy(text("Next")).parent().click();
    $$("input").findBy(attribute("formcontrolname", "firstName")).setValue("Adam");
    $$("input").findBy(attribute("formcontrolname", "lastName")).setValue("Kochanowski");
    $$("input").findBy(attribute("formcontrolname", "street")).setValue("Sezamkowa");
    $$("input").findBy(attribute("formcontrolname", "city")).setValue("Warszawa");
    $$("input").findBy(attribute("formcontrolname", "country")).setValue("Polska");
    $$("input").findBy(attribute("formcontrolname", "streetNumber")).setValue("12");
    $$("input").findBy(attribute("formcontrolname", "postalCode")).setValue("22-222");
    $$("mat-select").findBy(attribute("formcontrolname", "timeZone")).click();
    $$(".mat-option-text").findBy(text("Europe/Warsaw")).click();
    $(".register-button").click();
    $$("span").findBy(text("Confirm")).click();
    $(".form-title").shouldNotHave(text("Create your account"));
  }

  @Test
  @DisplayName("Should properly register account with company details")
  void shouldProperlyRegisterUserWithCompany() {
    open("/");
    $(".title-text").shouldHave(text("Wooden Furniture"));
    $$("span").findBy(text("Sign up")).click();
    $(".form-title").shouldHave(text("Create your account"));
    $$("input").findBy(attribute("formcontrolname", "email")).setValue("companion@ssbd.com");
    $$("input").findBy(attribute("formcontrolname", "login")).setValue("companion");
    $$("input").findBy(attribute("formcontrolname", "password")).setValue("Company321!");
    $$("input").findBy(attribute("formcontrolname", "confirmPassword")).setValue("Company321!");
    $$(".mat-button-wrapper").findBy(text("Next")).parent().click();
    $$("input").findBy(attribute("formcontrolname", "firstName")).setValue("Helmut");
    $$("input").findBy(attribute("formcontrolname", "lastName")).setValue("Moltke");
    $$("input").findBy(attribute("formcontrolname", "street")).setValue("Prosta");
    $$("input").findBy(attribute("formcontrolname", "city")).setValue("Kielce");
    $$("input").findBy(attribute("formcontrolname", "country")).setValue("Polska");
    $$("input").findBy(attribute("formcontrolname", "streetNumber")).setValue("1");
    $$("input").findBy(attribute("formcontrolname", "postalCode")).setValue("11-111");
    $$("mat-select").findBy(attribute("formcontrolname", "timeZone")).click();
    $$(".mat-option-text").findBy(text("Europe/Warsaw")).click();
    $("mat-checkbox").click();
    $$("input").findBy(attribute("formcontrolname", "nip")).setValue("1234567890");
    $$("input").findBy(attribute("formcontrolname", "companyName")).setValue("MebloPOL");
    $(".register-button").click();
    $$("span").findBy(text("Confirm")).click();
    $(".form-title").shouldNotHave(text("Create your account"));
  }
}
