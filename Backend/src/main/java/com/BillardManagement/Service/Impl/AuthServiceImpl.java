package com.BillardManagement.Service.Impl;

import com.BillardManagement.DTO.Request.LoginRequest;
<<<<<<< Updated upstream
import com.BillardManagement.DTO.Response.EmployeeLoginResponse;
=======
>>>>>>> Stashed changes
import com.BillardManagement.DTO.Response.EmployeeUserView;
import com.BillardManagement.DTO.Response.LoginResponse;
import com.BillardManagement.Entity.*;
import com.BillardManagement.Repository.AdminRepo;
import com.BillardManagement.Repository.CustomerRepo;
import com.BillardManagement.Repository.EmployeeAccountRepo;

import com.BillardManagement.Repository.EmployeeRepo;
import com.BillardManagement.Service.AuthService;
import com.BillardManagement.Util.PasswordUtil;
<<<<<<< Updated upstream
import jakarta.transaction.Transactional;
=======
import org.springframework.transaction.annotation.Transactional;
>>>>>>> Stashed changes
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AdminRepo adminRepo;
    private final CustomerRepo customerRepo;
    private final EmployeeAccountRepo employeeAccountRepo;
<<<<<<< Updated upstream
    private final EmployeeRepo employeeRepo;
=======
>>>>>>> Stashed changes

    @Override
    public LoginResponse login(LoginRequest request) {
        String username = request.getEmail();
        String password = request.getPassword();

        // thử kiểm tra theo thứ tự: admin → employee → customer
        Optional<Admin> adminOpt = adminRepo.findByEmail(username);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (PasswordUtil.matches(password, admin.getPasswordHash())) {
                return new LoginResponse(true, "Đăng nhập admin thành công", "TOKEN_ADMIN", admin);
            }
            return new LoginResponse(false, "Sai mật khẩu admin", null, null);
        }

<<<<<<< Updated upstream
        EmployeeLoginResponse empRes = loginEmployee(username, password);
        if (empRes.isSuccess()) {
            return new LoginResponse(true, empRes.getMessage(), empRes.getAccessToken(), empRes.getUser());
        }
        if ("Sai mật khẩu nhân viên".equals(empRes.getMessage()) || "Tài khoản đã bị khóa".equals(empRes.getMessage())) {
            return new LoginResponse(false, empRes.getMessage(), null, null);
=======
        Optional<Employeeaccount> empOpt = employeeAccountRepo.findEmployeeaccountByUsernameAndPasswordHash(username, password);
        if (empOpt.isPresent()) {
            return loginEmployee(username, password);
>>>>>>> Stashed changes
        }

        Optional<Customer> customerOpt = customerRepo.findByEmailAndPassword(username, password);
        if (customerOpt.isPresent()) {
            return new LoginResponse(true, "Đăng nhập khách hàng thành công", "TOKEN_CUSTOMER", customerOpt.get());
        }

        return new LoginResponse(false, "Không tìm thấy tài khoản phù hợp", null, null);
    }

<<<<<<< Updated upstream
    @Transactional
    public EmployeeLoginResponse loginEmployee(String username, String rawPassword) {
        var accOpt = employeeAccountRepo.findByUsername(username);
        if (accOpt.isEmpty()) return EmployeeLoginResponse.fail("Không tìm thấy tài khoản nhân viên");

        var acc = accOpt.get();
        if (Boolean.FALSE.equals(acc.getIsActive())) {
            return EmployeeLoginResponse.fail("Tài khoản đã bị khóa");
        }
        if (!PasswordUtil.matches(rawPassword, acc.getPasswordHash())) {
            return EmployeeLoginResponse.fail("Sai mật khẩu nhân viên");
        }

        // 3) Lấy thông tin nhân viên (có thể null nếu tài khoản chưa gắn employee)
        Employee emp = null;
        if (acc.getEmployeeID() != null) {
            emp = (Employee) employeeRepo.findById(acc.getEmployeeID()).orElse(null);
        }

        // 4) Map sang view cho FE
        EmployeeUserView view = EmployeeUserView.builder()
                .accountId(asLong(acc.getId()))                               // tránh acc.getId() nếu không có
                .employeeId(emp != null ? asLong(emp.getId()) : null)
                .clubId(acc.getClubID() != null ? asLong(acc.getClubID().getId())
                        : (emp != null ? asLong(emp.getClubID().getId()) : null))         // đóng đủ )) trước khi sang dòng mới
                .username(acc.getUsername())                                      // username nên lấy từ account
                .fullName(emp != null ? emp.getEmployeeName() : null)
                .email(emp != null ? emp.getEmail() : acc.getUsername())          // fallback nếu cần
                .role("STAFF")
                .build();

        // 5) Trả về response thống nhất với FE
        return EmployeeLoginResponse.success(
                "Đăng nhập nhân viên thành công",
                "TOKEN_STAFF",
                view
        );
    }

    private Long asLong(Integer v) { return v == null ? null : v.longValue(); }
=======
    @Transactional(readOnly = true)
    public LoginResponse loginEmployee(String username, String password) {
        Optional<Employeeaccount> empOpt = employeeAccountRepo
                .findEmployeeaccountByUsernameAndPasswordHash(username, password);

        Employeeaccount acc = empOpt.get();

        Employee emp = acc.getEmployeeID();
        Billardclub club = acc.getClubID();

        EmployeeUserView user = EmployeeUserView.builder()
                .accountId(acc.getId() != null ? acc.getId().longValue() : null)
                .employeeId(emp != null ? (long) emp.getId() : null)
                .clubId(club != null ? (long) club.getId() : null)
                .username(acc.getUsername())
                .fullName(emp != null ? emp.getEmployeeName() : acc.getUsername())
                .email(acc.getUsername())
                .role("STAFF")
                .build();

        return new LoginResponse(true, "Đăng nhập nhân viên thành công", "TOKEN_STAFF", user);
    }
>>>>>>> Stashed changes
}