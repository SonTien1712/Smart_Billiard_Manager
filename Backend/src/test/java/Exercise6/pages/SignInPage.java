package Exercise6.pages;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class SignInPage extends BasePage {
    private static final String URL = "/signin";
    private final By emailInput = By.id("email");
    private final By passwordInput = By.id("password");
    private final By submitBtn = By.cssSelector("button[type='submit']");
    private final By errorAlert = By.cssSelector(".alert-destructive");

    public SignInPage(WebDriver driver) { super(driver); }

    public SignInPage open() {
        driver.get(BASE_URL + URL);
        return this;
    }

    public SignInPage login(String email, String password) {
        type(emailInput, email);
        type(passwordInput, password);
        click(submitBtn);
        return this;
    }

    public String getErrorMessage() {
        return waitForVisibility(errorAlert).getText();
    }

    public boolean waitForRedirect() {
        return wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/dashboard"),
                ExpectedConditions.urlContains("/premium")
        )) != null;
    }

    public String dismissLoginAlertIfPresent() {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String text = alert.getText();
            alert.accept();
            return text;
        } catch (TimeoutException e) {
            return null;
        }
    }

    public boolean isTokenStored() {
        Object token = executeScript("return sessionStorage.getItem('accessToken');");
        return token != null;
    }
}
