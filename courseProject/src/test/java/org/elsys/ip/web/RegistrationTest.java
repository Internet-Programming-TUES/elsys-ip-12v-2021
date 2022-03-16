package org.elsys.ip.web;

import org.elsys.ip.selenium.SeleniumConfig;
import org.elsys.ip.web.pageobjects.HomePage;
import org.elsys.ip.web.pageobjects.LoginPage;
import org.elsys.ip.web.pageobjects.RegistrationPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class RegistrationTest {
    private WebDriver driver;

    @LocalServerPort
    private int port;

    private String baseAddress;

    @BeforeEach
    public void setUp() {
        driver = new SeleniumConfig().getDriver();
        baseAddress = "http://localhost:" + port + "/";
    }

    @AfterEach
    public void tearDown() {
        driver.close();
    }

    @Test
    public void registration() throws InterruptedException {
        driver.get(baseAddress);
        HomePage homePage = PageFactory.initElements(driver, HomePage.class);

        RegistrationPage registrationPage = homePage.register();
        homePage = registrationPage.register("First Name", "Last Name", "email@email.com", "password");
        LoginPage loginPage = homePage.login();
        homePage = loginPage.login("email@email.com", "password");
        assertThat(homePage.isAuthenticated()).isTrue();
    }

    @Test
    public void registrationErrors() throws InterruptedException {
        driver.get(baseAddress);
        HomePage homePage = PageFactory.initElements(driver, HomePage.class);

        RegistrationPage registrationPage = homePage.register();
        registrationPage.register("", "", "", "", "1");
        assertThat(registrationPage.getFirstNameErrors()).containsExactly("must not be empty");
        assertThat(registrationPage.getLastNameErrors()).containsExactly("must not be empty");
        assertThat(registrationPage.getEmailNameErrors()).containsExactlyInAnyOrder("must not be empty", "Invalid email");
        assertThat(registrationPage.getPasswordErrors()).containsExactly("must not be empty");
        assertThat(registrationPage.getGlobalErrors()).containsExactly("Passwords don't match");

        registrationPage.register("First Name", "", "", "", "1");

        assertThat(registrationPage.getFirstNameErrors()).hasSize(0);
        assertThat(registrationPage.getLastNameErrors()).containsExactly("must not be empty");
        assertThat(registrationPage.getEmailNameErrors()).containsExactlyInAnyOrder("must not be empty", "Invalid email");
        assertThat(registrationPage.getPasswordErrors()).containsExactly("must not be empty");
        assertThat(registrationPage.getGlobalErrors()).containsExactly("Passwords don't match");

        registrationPage.register("First Name", "Last Name", "invalid", "1", "1");
        assertThat(registrationPage.getFirstNameErrors()).hasSize(0);
        assertThat(registrationPage.getLastNameErrors()).hasSize(0);
        assertThat(registrationPage.getEmailNameErrors()).containsExactly("Invalid email");
        assertThat(registrationPage.getPasswordErrors()).hasSize(0);
        assertThat(registrationPage.getGlobalErrors()).hasSize(0);
    }
}
