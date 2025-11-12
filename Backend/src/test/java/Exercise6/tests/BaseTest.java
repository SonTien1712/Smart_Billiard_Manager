package Exercise6.tests;

import Exercise6.utils.DriverFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;


public abstract class BaseTest {
    protected static WebDriver driver;
    protected static WebDriverWait wait;

    protected static final String BASE_URL =
            System.getProperty("app.baseUrl", "http://localhost:3000");

    @BeforeAll
    static void setup() {
        driver = DriverFactory.createDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }
    @AfterAll
    static void tearDown() { if (driver != null) driver.quit(); }
}
