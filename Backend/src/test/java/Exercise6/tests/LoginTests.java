package Exercise6.tests;

import Exercise6.pages.SignInPage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginTests extends BaseTest {

    private static SignInPage signIn;

    @BeforeAll
    static void initPage() { signIn = new SignInPage(driver); }

    @ParameterizedTest(name = "Success #{index}: {0}")
    @CsvFileSource(resources = "/data/login-data6.csv", numLinesToSkip = 1)
    void shouldLoginWithValidAccount(String email, String password, String expectedPath) {
        signIn.open().login(email, password);
        assertTrue(signIn.waitForRedirect(), "Should navigate to dashboard or premium");
        assertTrue(driver.getCurrentUrl().contains(expectedPath));
        assertTrue(signIn.isTokenStored());
    }

    @ParameterizedTest(name = "Invalid #{index}: {0}")
    @CsvSource({
            "admin@billard.com, WrongPass, Sai mật khẩu admin",
            "customer@billard.com, WrongPass, Không tìm thấy tài khoản phù hợp",
            "staff@billard.com, AnyPassword, Tài khoản khách hàng đã hết hạn, vui lòng gia hạn trước khi đăng nhập nhân viên"
    })
    void shouldShowErrorForInvalidCredentials(String email, String password) {
        signIn.open().login(email, password);

        String alertMsg = signIn.dismissLoginAlertIfPresent();
        if (alertMsg != null) {
            assertTrue(alertMsg.toLowerCase().contains("login failed"));
        } else {
            assertTrue(signIn.getErrorMessage().length() > 0);
        }

        assertTrue(driver.getCurrentUrl().endsWith("/signin"));
    }

}
