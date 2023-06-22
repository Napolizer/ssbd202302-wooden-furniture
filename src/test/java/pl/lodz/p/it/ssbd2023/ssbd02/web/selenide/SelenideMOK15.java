package pl.lodz.p.it.ssbd2023.ssbd02.web.selenide;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Map;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;

public class SelenideMOK15 {

  @BeforeEach
  public void setUp() {
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
  void shouldProperlyGiveEmailDoSendNewPassword() {
    open("/");
    $(".title-text").shouldHave(text("Wooden Furniture"));
    $$("span").findBy(text("Login")).click();
    $(".forgot-password-link").click();
    $$("input").findBy(attribute("formcontrolname", "email")).setValue("adam.mickiewicz@ssbd.com");
    $(".mat-button").click();
    $("body").shouldHave(text("Login"));
  }
}
