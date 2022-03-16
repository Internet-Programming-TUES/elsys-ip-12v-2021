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

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
        homePage.register();
        RegistrationPage registrationPage = PageFactory.initElements(driver, RegistrationPage.class);
        registrationPage.register("First Name", "Last Name", "email@email.com", "password");
        homePage = PageFactory.initElements(driver, HomePage.class);
        homePage.login();
        LoginPage loginPage = PageFactory.initElements(driver, LoginPage.class);
        loginPage.login("email@email.com", "password");
        homePage = PageFactory.initElements(driver, HomePage.class);
        assertThat(homePage.isAuthenticated()).isTrue();
    }
}
