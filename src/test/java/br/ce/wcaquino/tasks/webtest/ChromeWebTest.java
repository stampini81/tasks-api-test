package br.ce.wcaquino.tasks.webtest;

import static org.junit.Assert.assertTrue;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ChromeWebTest {

    private WebDriver driver;

    @Before
    public void setup() {
        Assume.assumeTrue("Chrome nao esta instalado.", chromeInstalado());
        Assume.assumeTrue("tasks-backend indisponivel para o teste web.", endpointDisponivel("http://localhost:8001/tasks-backend/"));

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1400,900");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void deveAbrirPaginaInicialDoBackend() {
        driver.get("http://localhost:8001/tasks-backend/");

        String bodyText = driver.findElement(By.tagName("body")).getText();
        assertTrue(bodyText.contains("Hello World!"));
    }

    private boolean chromeInstalado() {
        return new java.io.File("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe").exists()
            || new java.io.File("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe").exists();
    }

    private boolean endpointDisponivel(String url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            connection.setRequestMethod("GET");
            return connection.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
