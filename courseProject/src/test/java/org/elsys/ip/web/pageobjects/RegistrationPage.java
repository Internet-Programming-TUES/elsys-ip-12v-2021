package org.elsys.ip.web.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class RegistrationPage {
    @FindBy(how = How.ID, using = "firstName")
    private WebElement firstName;

    @FindBy(how = How.ID, using = "lastName")
    private WebElement lastName;

    @FindBy(how = How.ID, using = "email")
    private WebElement email;

    @FindBy(how = How.ID, using = "password")
    private WebElement password;

    @FindBy(how = How.ID, using = "matchingPassword")
    private WebElement matchingPassword;

    @FindBy(how = How.TAG_NAME, using = "button")
    private WebElement submitButton;

    public void register(
            String firstName,
            String lastName,
            String email,
            String password) {
        this.firstName.sendKeys(firstName);
        this.lastName.sendKeys(lastName);
        this.email.sendKeys(email);
        this.password.sendKeys(password);
        this.matchingPassword.sendKeys(password);
        submitButton.click();
    }
}