package pl.lodz.p.it.ssbd2023.ssbd02.web.selenide;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.url;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openqa.selenium.support.ui.ExpectedConditions.titleContains;
import static org.openqa.selenium.support.ui.ExpectedConditions.urlContains;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testcontainers.containers.BrowserWebDriverContainer;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;

//@MicroShedTest
//@SharedContainerConfig(AppContainerConfig.class)
class SelenideIT {
//  public static BrowserWebDriverContainer<?> chrome = AppContainerConfig.chrome;

  @BeforeEach
  public void setUp() {
//    RemoteWebDriver driver = chrome.getWebDriver();
//    WebDriverRunner.setWebDriver(driver);
    ChromeOptions options = new ChromeOptions();
    options.addArguments("start-maximized");
    options.addArguments("--no-sandbox");
    options.addArguments("--ignore-certificate-errors");
    options.addArguments("--lang=en");
    options.setExperimentalOption("prefs", Map.of("intl.accept_languages", "en"));
    WebDriverRunner.setWebDriver(new ChromeDriver(options));
//    Configuration.baseUrl = "http://frontend";
    Configuration.baseUrl = "http://localhost:4200";
  }

  @AfterEach
  public void tearDown() {
    WebDriverRunner.closeWebDriver();
  }

  @Test
  void showSelenideUsers() {
    open("/");
    $(".title-text").shouldHave(text("Wooden Furniture"));
    $$("span").findBy(text("Login")).click();
    $$("input").findBy(attribute("data-placeholder", "login")).setValue("client");
    $$("input").findBy(attribute("data-placeholder", "password")).setValue("Student123!");
    $(".login-button").click();
    webdriver().shouldHave(urlContaining("/product"));
  }
}
