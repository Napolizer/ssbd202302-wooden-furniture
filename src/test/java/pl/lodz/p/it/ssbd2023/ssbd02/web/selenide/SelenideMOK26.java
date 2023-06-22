package pl.lodz.p.it.ssbd2023.ssbd02.web.selenide;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
class SelenideMOK26 {
  public static BrowserWebDriverContainer<?> chrome = AppContainerConfig.chrome;
  @BeforeEach
  public void setUp() {
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
  void shouldProperlyChangeAccessLevelToEmployeeAndBackToSalesRep() {
    open("/");
    $(".title-text").shouldHave(text("Wooden Furniture"));
    $$("span").findBy(text("Login")).click();
    $$("input").findBy(attribute("data-placeholder", "login")).setValue("administrator");
    $$("input").findBy(attribute("data-placeholder", "password")).setValue("Student123!");
    $(".mat-focus-indicator .login-button").click();
    sleep(8000);
    $$(".mat-icon").filterBy(Condition.text("menu")).first().click();
    $$(".mat-menu-item").filterBy(Condition.text("admin_panel_settings")).first().click();
    sleep(4000);
    SelenideElement accountRow = $$(".mat-row").filterBy(text("salesrep")).first();
    accountRow.$(".action-menu-account").click();
    $(".mat-menu-item-role").hover();
    $(".change-role-button").click();
    $(".mat-select-change-role").click();
    $$(".mat-option-text").findBy(text("Employee")).click();
    $(".confirm-change-role-button").click();
    $$(".mat-raised-button").filterBy(Condition.text("Confirm")).last().click();
    sleep(10000);
    accountRow.$(".show").click();
    webdriver().shouldHave(urlContaining("/account"));
    $(By.tagName("body")).shouldHave(text("Employee"));
    $(".change-role-button").click();
    sleep(2000);
    $(".change-role-salesrep").click();
    $$(".mat-raised-button").filterBy(Condition.text("Confirm")).last().click();
    sleep(5000);
    $(By.tagName("body")).shouldHave(text("Sales Representative"));
  }
}
