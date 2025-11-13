package UnitTest;

import com.BillardManagement.DTO.Request.LoginRequest;
import com.BillardManagement.DTO.Response.EmployeeUserView;
import com.BillardManagement.DTO.Response.LoginResponse;
import com.BillardManagement.Entity.Admin;
import com.BillardManagement.Entity.Billardclub;
import com.BillardManagement.Entity.Customer;
import com.BillardManagement.Entity.Employee;
import com.BillardManagement.Entity.Employeeaccount;
import com.BillardManagement.Repository.AdminRepo;
import com.BillardManagement.Repository.CustomerRepo;
import com.BillardManagement.Repository.EmployeeAccountRepo;
import com.BillardManagement.Repository.EmployeeRepo;
import com.BillardManagement.Service.Impl.AuthServiceImpl;
import com.BillardManagement.Util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private AdminRepo adminRepo;

    @Mock
    private CustomerRepo customerRepo;

    @Mock
    private EmployeeAccountRepo employeeAccountRepo;

    @Mock
    private EmployeeRepo employeeRepo;

    @Mock
    private JwtUtil jwtUtil;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(adminRepo, customerRepo, employeeAccountRepo, employeeRepo, jwtUtil);
        lenient().when(jwtUtil.generateAccessToken(anyMap())).thenReturn("access-token");
        lenient().when(jwtUtil.generateRefreshToken(anyMap())).thenReturn("refresh-token");
    }

    @ParameterizedTest(name = "{0} -> {3}")
    @CsvFileSource(resources = "/data/loginUnit-data.csv", numLinesToSkip = 1, encoding = "UTF-8")
    void login_shouldMatchExpectedScenario(String caseId,
                                           String identifier,
                                           String password,
                                           String scenarioType,
                                           boolean expectedSuccess,
                                           String expectedMessage) {
        prepareScenario(scenarioType, identifier, password);

        LoginRequest request = new LoginRequest();
        request.setEmail(identifier);
        request.setPassword(password);

        LoginResponse response = authService.login(request);

        assertEquals(expectedSuccess, response.isSuccess(), caseId + " success");
        assertEquals(expectedMessage.trim(), response.getMessage().trim(), caseId + " message");
        if (expectedSuccess) {
            assertEquals("access-token", response.getAccessToken(), caseId + " access token");
            assertEquals("refresh-token", response.getRefreshToken(), caseId + " refresh token");
        } else {
            assertNull(response.getAccessToken(), caseId + " access token");
            assertNull(response.getRefreshToken(), caseId + " refresh token");
        }

        assertUserPayload(caseId, scenarioType, expectedSuccess, response.getUser());
    }

    @Test
    void loginEmployee_shouldReturnErrorWhenAccountMissing() {
        String username = "missing_staff";
        String password = "noPass";
        when(employeeAccountRepo.findEmployeeaccountByUsernameAndPasswordHash(username, password))
                .thenReturn(Optional.empty());

        LoginResponse response = authService.loginEmployee(username, password);

        assertFalse(response.isSuccess());
        assertEquals("Không tìm thấy tài khoản nhân viên", response.getMessage());
        assertNull(response.getAccessToken());
        assertNull(response.getRefreshToken());
        assertNull(response.getUser());
    }

    private void prepareScenario(String scenarioType, String identifier, String password) {
        lenient().when(adminRepo.findByEmail(identifier)).thenReturn(Optional.empty());
        lenient().when(employeeAccountRepo.findEmployeeaccountByUsernameAndPasswordHash(identifier, password))
                .thenReturn(Optional.empty());
        lenient().when(customerRepo.findByEmailAndPassword(identifier, password)).thenReturn(Optional.empty());

        switch (scenarioType) {
            case "ADMIN_SUCCESS": {
                Admin admin = buildAdmin(identifier, password);
                when(adminRepo.findByEmail(identifier)).thenReturn(Optional.of(admin));
                break;
            }
            case "ADMIN_WRONG_PASSWORD": {
                Admin admin = buildAdmin(identifier, "storedHash");
                when(adminRepo.findByEmail(identifier)).thenReturn(Optional.of(admin));
                break;
            }
            case "EMPLOYEE_SUCCESS": {
                Customer owner = buildCustomer(100, LocalDate.now());
                Billardclub club = buildClub(200, owner);
                Employee employee = buildEmployee(300, owner, club);
                Employeeaccount account = buildEmployeeAccount(400, identifier, password, owner, club, employee);
                when(employeeAccountRepo.findEmployeeaccountByUsernameAndPasswordHash(identifier, password))
                        .thenReturn(Optional.of(account));
                break;
            }
            case "EMPLOYEE_NO_OWNER": {
                Billardclub club = buildClub(201, null);
                Employeeaccount account = buildEmployeeAccount(401, identifier, password, null, club, null);
                when(employeeAccountRepo.findEmployeeaccountByUsernameAndPasswordHash(identifier, password))
                        .thenReturn(Optional.of(account));
                break;
            }
            case "EMPLOYEE_CUSTOMER_EXPIRED": {
                Customer owner = buildCustomer(102, LocalDate.now().minusDays(1));
                Billardclub club = buildClub(202, owner);
                Employee employee = buildEmployee(302, owner, club);
                Employeeaccount account = buildEmployeeAccount(402, identifier, password, owner, club, employee);
                when(employeeAccountRepo.findEmployeeaccountByUsernameAndPasswordHash(identifier, password))
                        .thenReturn(Optional.of(account));
                break;
            }
            case "EMPLOYEE_CUSTOMER_EXPIRY_NULL": {
                Customer owner = buildCustomer(103, null);
                Billardclub club = buildClub(203, owner);
                Employee employee = buildEmployee(303, owner, club);
                Employeeaccount account = buildEmployeeAccount(403, identifier, password, owner, club, employee);
                when(employeeAccountRepo.findEmployeeaccountByUsernameAndPasswordHash(identifier, password))
                        .thenReturn(Optional.of(account));
                break;
            }
            case "EMPLOYEE_CLUB_NULL": {
                Customer owner = buildCustomer(104, LocalDate.now());
                Employee employee = buildEmployee(304, owner, null);
                Employeeaccount account = buildEmployeeAccount(404, identifier, password, owner, null, employee);
                lenient().when(employeeAccountRepo.findEmployeeaccountByUsernameAndPasswordHash(identifier, password))
                        .thenReturn(Optional.of(account));
                break;
            }
            case "EMPLOYEE_EMP_NULL": {
                Customer owner = buildCustomer(105, LocalDate.now());
                Billardclub club = buildClub(205, owner);
                Employeeaccount account = buildEmployeeAccount(405, identifier, password, owner, club, null);
                lenient().when(employeeAccountRepo.findEmployeeaccountByUsernameAndPasswordHash(identifier, password))
                        .thenReturn(Optional.of(account));
                break;
            }
            case "EMPLOYEE_EMP_ID_NULL": {
                Customer owner = buildCustomer(106, LocalDate.now());
                Billardclub club = buildClub(206, owner);
                Employee employee = buildEmployee(0, owner, club);
                employee.setId(null);
                Employeeaccount account = buildEmployeeAccount(406, identifier, password, owner, club, employee);
                lenient().when(employeeAccountRepo.findEmployeeaccountByUsernameAndPasswordHash(identifier, password))
                        .thenReturn(Optional.of(account));
                break;
            }
            case "EMPLOYEE_CLUB_ID_NULL": {
                Customer owner = buildCustomer(107, LocalDate.now());
                Billardclub club = buildClub(0, owner);
                club.setId(null);
                Employee employee = buildEmployee(307, owner, club);
                Employeeaccount account = buildEmployeeAccount(407, identifier, password, owner, club, employee);
                lenient().when(employeeAccountRepo.findEmployeeaccountByUsernameAndPasswordHash(identifier, password))
                        .thenReturn(Optional.of(account));
                break;
            }
            case "EMPLOYEE_ACCOUNT_ID_NULL": {
                Customer owner = buildCustomer(108, LocalDate.now());
                Billardclub club = buildClub(208, owner);
                Employee employee = buildEmployee(308, owner, club);
                Employeeaccount account = buildEmployeeAccount(0, identifier, password, owner, club, employee);
                account.setId(null);
                lenient().when(employeeAccountRepo.findEmployeeaccountByUsernameAndPasswordHash(identifier, password))
                        .thenReturn(Optional.of(account));
                break;
            }
            case "CUSTOMER_SUCCESS": {
                Customer customer = buildCustomer(500, LocalDate.now().plusDays(15));
                customer.setEmail(identifier);
                customer.setPassword(password);
                when(customerRepo.findByEmailAndPassword(identifier, password)).thenReturn(Optional.of(customer));
                break;
            }
            case "NOT_FOUND":
                // defaults already stubbed to Optional.empty()
                break;
            default:
                throw new IllegalArgumentException("Unsupported scenario: " + scenarioType);
        }
    }

    private void assertUserPayload(String caseId, String scenarioType, boolean expectedSuccess, Object payload) {
        if (!expectedSuccess) {
            assertNull(payload, caseId + " user payload");
            return;
        }

        switch (scenarioType) {
            case "ADMIN_SUCCESS":
                assertTrue(payload instanceof Admin, caseId + " should expose admin entity");
                break;
            case "EMPLOYEE_SUCCESS":
            case "EMPLOYEE_CLUB_NULL":
            case "EMPLOYEE_EMP_NULL":
            case "EMPLOYEE_EMP_ID_NULL":
            case "EMPLOYEE_CLUB_ID_NULL":
            case "EMPLOYEE_ACCOUNT_ID_NULL":
                assertTrue(payload instanceof EmployeeUserView, caseId + " should expose employee view");
                break;
            case "CUSTOMER_SUCCESS":
                assertTrue(payload instanceof Customer, caseId + " should expose customer entity");
                break;
            default:
                assertNull(payload, caseId + " should not expose user payload");
                break;
        }
    }

    private Admin buildAdmin(String email, String passwordHash) {
        Admin admin = new Admin();
        admin.setId(1);
        admin.setEmail(email);
        admin.setUsername(email);
        admin.setPasswordHash(passwordHash);
        return admin;
    }

    private Customer buildCustomer(int id, LocalDate expiryDate) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setCustomerName("Customer " + id);
        customer.setEmail("customer" + id + "@example.com");
        customer.setPhoneNumber("0900000" + id);
        customer.setExpiryDate(expiryDate);
        return customer;
    }

    private Billardclub buildClub(int id, Customer owner) {
        Billardclub club = new Billardclub();
        club.setId(id);
        club.setClubName("Club " + id);
        club.setAddress("Address " + id);
        club.setPhoneNumber("0987000" + id);
        club.setCustomerID(owner != null ? owner.getId() : null);
        return club;
    }

    private Employee buildEmployee(int id, Customer owner, Billardclub club) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setEmployeeName("Employee " + id);
        employee.setEmployeeType("FULL_TIME");
        employee.setPhoneNumber("0912000" + id);
        employee.setEmail("employee" + id + "@example.com");
        employee.setCustomerID(owner);
        employee.setClubID(club);
        return employee;
    }

    private Employeeaccount buildEmployeeAccount(int id,
                                                 String username,
                                                 String password,
                                                 Customer owner,
                                                 Billardclub club,
                                                 Employee employee) {
        Employeeaccount account = new Employeeaccount();
        account.setId(id);
        account.setUsername(username);
        account.setPasswordHash(password);
        account.setCustomerID(owner);
        account.setClubID(club);
        account.setEmployeeID(employee);
        return account;
    }
}
