package com.BillardManagement.Service.Impl;

import com.BillardManagement.DTO.Request.LoginRequest;
import com.BillardManagement.DTO.Response.LoginResponse;
import com.BillardManagement.DTO.Response.EmployeeUserView;
import com.BillardManagement.Entity.Admin;
import com.BillardManagement.Entity.Customer;
import com.BillardManagement.Entity.Employeeaccount;
import com.BillardManagement.Repository.AdminRepo;
import com.BillardManagement.Repository.CustomerRepo;
import com.BillardManagement.Repository.EmployeeAccountRepo;

import com.BillardManagement.Service.AuthService;
import com.BillardManagement.Util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AdminRepo adminRepo;
    private final CustomerRepo customerRepo;
    private final EmployeeAccountRepo employeeRepo;

    // Nghiệp vụ đăng nhập (ưu tiên nhân viên)
    // - Nhận identifier (email/username) + password
    // - Thứ tự kiểm tra: admin → employee → customer
    // - Với nhân viên: cho phép đăng nhập bằng username hoặc email nhân viên
    // - Trả về LoginResponse chứa token mẫu và thông tin người dùng rút gọn
    @Override
    public LoginResponse login(LoginRequest request) {
        String identifier = request.getEmail(); // can be username or email
        String password = request.getPassword();

        // thử kiểm tra theo thứ tự: admin → employee → customer
        Optional<Admin> adminOpt = adminRepo.findByEmail(identifier);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (PasswordUtil.matches(password, admin.getPasswordHash())) {
                return new LoginResponse(true, "Đăng nhập admin thành công", "TOKEN_ADMIN", admin);
            }
            return new LoginResponse(false, "Sai mật khẩu admin", null, null);
        }

        // Prefer employee if identifier matches an employee username or email
        Optional<Employeeaccount> empOpt = employeeRepo.findByUsername(identifier);
        if (empOpt.isEmpty() && identifier != null && identifier.contains("@")) {
            empOpt = employeeRepo.findByEmployeeEmail(identifier);
        }
        if (empOpt.isPresent()) {
            Employeeaccount emp = empOpt.get();
            if (PasswordUtil.matches(password, emp.getPasswordHash())) {
                Optional<EmployeeUserView> viewOpt = identifier != null && identifier.contains("@")
                        ? employeeRepo.findViewByEmployeeEmail(identifier)
                        : employeeRepo.findWithEmployeeByUsername(identifier);
                return new LoginResponse(true, "Đăng nhập nhân viên thành công", "TOKEN_EMPLOYEE", viewOpt.orElse(null));
            }
            // If identified as employee, do not fall through to customer
            return new LoginResponse(false, "Sai mật khẩu nhân viên", null, null);
        }

        Optional<Customer> customerOpt = customerRepo.findByEmailAndPassword(identifier, password);
        if (customerOpt.isPresent()) {
            return new LoginResponse(true, "Đăng nhập khách hàng thành công", "TOKEN_CUSTOMER", customerOpt.get());
        }

        return new LoginResponse(false, "Không tìm thấy tài khoản phù hợp", null, null);
    }
}
