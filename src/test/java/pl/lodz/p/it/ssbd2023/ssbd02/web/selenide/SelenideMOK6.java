package pl.lodz.p.it.ssbd2023.ssbd02.web.selenide;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import java.time.Duration;
import org.junit.jupiter.api.*;
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
public class SelenideMOK6 {
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
    @DisplayName("Should properly remove employee access level from account")
    void shouldProperlyRemoveEmployeeAccessLevelFromAccount() {
        open("/");
        $(".title-text").shouldHave(text("Wooden Furniture"));
        $$("span").findBy(text("Login")).click();
        $$("input").findBy(attribute("data-placeholder", "login")).setValue("administrator");
        $$("input").findBy(attribute("data-placeholder", "password")).setValue("Student123!");
        $(".mat-focus-indicator .login-button").click();
        webdriver().shouldHave(urlContaining("/home"));
        sleep(7000);
        $$(".mat-icon").filterBy(Condition.text("menu")).first().click();
        $$(".mat-menu-item").filterBy(Condition.text("admin_panel_settings")).first().click();
        sleep(4000);
        SelenideElement accountRow = $$(".mat-row").filterBy(text("client2")).first();
        sleep(4000);
        accountRow.$(".action-menu-account").click();
        $(".mat-menu-item-role").hover();
        $(".add-role-button").click();
        $(".mat-select-role").click();
        $$(".mat-option-text").findBy(text("Employee")).click();
        $(".confirm-add-role-button").click();
        $$(".mat-raised-button").filterBy(Condition.text("Confirm")).last().click();
        sleep(15000);
        accountRow.$(".action-menu-account").click();
        $(".mat-menu-item-role").hover();
        $(".remove-role-button").click();
        $(".mat-select-role").click();
        $$(".mat-option-text").findBy(text("Employee")).click();
        $(".confirm-remove-role-button").click();
        $$(".mat-raised-button").filterBy(Condition.text("Confirm")).last().click();
        sleep(25000);
        accountRow.$(".show").click();
        webdriver().shouldHave(urlContaining("/account"));
        $(By.tagName("body")).shouldHave(text("Client"));
    }
}
