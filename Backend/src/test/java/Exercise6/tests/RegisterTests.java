package Exercise6.tests;

import Exercise6.pages.SignInPage;
import Exercise6.pages.SignUpPage;
import Exercise6.utils.UserData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RegisterTests extends BaseTest {

    private static SignUpPage signUp;
    private static SignInPage signIn;

    @BeforeAll
    static void initPages() {
        signUp = new SignUpPage(driver);
        signIn = new SignInPage(driver);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/register-data6.csv", numLinesToSkip = 1)
    void shouldRegisterSuccessfully(String name, String email, String phone,
                                    String address, String password, String confirm) {
        String uniqueEmail = email.replace("%TIME%", String.valueOf(System.currentTimeMillis()));
        UserData data = new UserData(name, uniqueEmail, phone, address, password, confirm);

        signUp.open().fillForm(data).submit();
        String alertText = signUp.acceptAlertAndGetText();
        assertTrue(alertText.toLowerCase().contains("thành công"));
        wait.until(ExpectedConditions.urlContains("/signin"));
    }

    @Test
    public void shouldWarnWhenPasswordsMismatch() {
        UserData data = new UserData("QA Test", "qa+" + System.currentTimeMillis() + "@billard.com",
                "0900000010", "789 Demo Rd", "Password1!", "Password2!");
        signUp.open().fillForm(data).submit();
        assertTrue(signUp.getInlineError().contains("Passwords do not match"));
    }
}

