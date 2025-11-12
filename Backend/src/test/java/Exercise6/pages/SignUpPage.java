package Exercise6.pages;

import Exercise6.utils.UserData;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class SignUpPage extends BasePage {
    private static final String URL = "/signup";
    private final By nameInput = By.id("name");
    private final By emailInput = By.id("email");
    private final By phoneInput = By.id("phone");
    private final By addressInput = By.id("address");
    private final By passwordInput = By.id("password");
    private final By confirmInput = By.id("confirmPassword");
    private final By submitBtn = By.cssSelector("button[type='submit']");
    private final By inlineError = By.cssSelector("div[role='alert']");

    public SignUpPage(WebDriver driver) { super(driver); }

    public SignUpPage open() { driver.get(BASE_URL + URL); return this; }

    public SignUpPage fillForm(UserData data) {
        type(nameInput, data.fullName());
        type(emailInput, data.email());
        type(phoneInput, data.phone());
        type(addressInput, data.address());
        type(passwordInput, data.password());
        type(confirmInput, data.confirmPassword());
        return this;
    }

    public SignUpPage submit() {
        click(submitBtn);
        return this;
    }

    public String acceptAlertAndGetText() {
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String text = alert.getText();
        alert.accept();
        return text;
    }

    public String getInlineError() {
        return waitForVisibility(inlineError).getText();
    }
}