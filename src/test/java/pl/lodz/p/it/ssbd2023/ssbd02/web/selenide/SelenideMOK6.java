package pl.lodz.p.it.ssbd2023.ssbd02.web.selenide;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class SelenideMOK6 {
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
