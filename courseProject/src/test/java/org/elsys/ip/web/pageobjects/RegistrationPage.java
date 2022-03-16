package org.elsys.ip.web.pageobjects;

import org.hibernate.cfg.annotations.reflection.internal.JPAXMLOverriddenAnnotationReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

import java.util.List;
import java.util.stream.Collectors;

public class RegistrationPage extends AbstractPage {
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

    public RegistrationPage(WebDriver driver) {
        super(driver);
    }

    public HomePage register(
            String firstName,
            String lastName,
            String email,
            String password) {
        register(firstName, lastName, email, password, password);
        return PageFactory.initElements(driver, HomePage.class);
    }

    public void register(
            String firstName,
            String lastName,
            String email,
            String password,
            String matchingPassword) {
        this.firstName.clear();
        this.lastName.clear();
        this.email.clear();
        this.password.clear();
        this.matchingPassword.clear();

        this.firstName.sendKeys(firstName);
        this.lastName.sendKeys(lastName);
        this.email.sendKeys(email);
        this.password.sendKeys(password);
        this.matchingPassword.sendKeys(matchingPassword);
        submitButton.click();
    }

    public List<String> getFirstNameErrors() {
        return driver.findElements(By.cssSelector("#firstName ~ p.error"))
                .stream().map(e -> e.getText()).collect(Collectors.toList());
    }

    public List<String> getLastNameErrors() {
        return driver.findElements(By.cssSelector("#lastName ~ p.error"))
                .stream().map(e -> e.getText()).collect(Collectors.toList());
    }

    public List<String> getEmailNameErrors() {
        return driver.findElements(By.cssSelector("#email ~ p.error"))
                .stream().map(e -> e.getText()).collect(Collectors.toList());
    }

    public List<String> getPasswordErrors() {
        return driver.findElements(By.cssSelector("#password ~ p.error"))
                .stream().map(e -> e.getText()).collect(Collectors.toList());
    }

    public List<String> getGlobalErrors() {
        return driver.findElements(By.cssSelector("form > p.error"))
                .stream().map(e -> e.getText()).collect(Collectors.toList());
    }
}
