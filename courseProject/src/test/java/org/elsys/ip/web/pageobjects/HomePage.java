package org.elsys.ip.web.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

public class HomePage extends AbstractPage {
    @FindBy(how = How.CLASS_NAME, using = "registerButton")
    private WebElement registrationButton;

    @FindBy(how = How.CLASS_NAME, using = "loginButton")
    private WebElement loginButton;

    @FindBy(how = How.ID, using = "logoutButton")
    private WebElement logoutButton;

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public RegistrationPage register() {
        registrationButton.click();
        return PageFactory.initElements(driver, RegistrationPage.class);
    }

    public LoginPage login() {
        loginButton.click();
        return PageFactory.initElements(driver, LoginPage.class);
    }

    public boolean isAuthenticated() {
        return driver.findElements(By.id("logoutButton")).size() == 1 &&
                driver.findElements(By.className("loginButton")).size() == 0;
    }

    public void logout() {
        logoutButton.click();
    }
}
