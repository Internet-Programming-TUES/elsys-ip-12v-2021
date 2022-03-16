package org.elsys.ip.web;

import org.elsys.ip.selenium.SeleniumConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

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
        driver.findElement(By.className("registerButton")).click();
        driver.findElement(By.id("firstName")).sendKeys("First Name");
        Thread.sleep(10000);
    }
}
