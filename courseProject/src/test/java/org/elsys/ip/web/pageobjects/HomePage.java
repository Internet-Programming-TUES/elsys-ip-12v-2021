package org.elsys.ip.web.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class HomePage {
    @FindBy(how = How.CLASS_NAME, using = "registerButton")
    private WebElement registrationButton;

    @FindBy(how = How.CLASS_NAME, using = "loginButton")
    private WebElement loginButton;

    @FindBy(how = How.ID, using = "logoutButton")
    private WebElement logoutButton;

    private WebDriver driver;

    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    public void register() {
        registrationButton.click();
    }

    public void login() {
        loginButton.click();
    }

    public boolean isAuthenticated() {
        return driver.findElements(By.id("logoutButton")).size() == 1 &&
                driver.findElements(By.className("loginButton")).size() == 0;
    }

    public void logout() {
        logoutButton.click();
    }
}
