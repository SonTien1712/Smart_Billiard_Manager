package com.BillardManagement.Service.Impl;

import com.BillardManagement.DTO.Request.LoginRequest;
import com.BillardManagement.DTO.Response.LoginResponse;
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

        Optional<Employeeaccount> empOpt = employeeRepo.findEmployeeaccountByUsernameAndPasswordHash(username, password);
        if (empOpt.isPresent()) {
            Employeeaccount emp = empOpt.get();
            if (PasswordUtil.matches(password, emp.getPasswordHash())) {
                return new LoginResponse(true, "Đăng nhập nhân viên thành công", "TOKEN_EMPLOYEE", emp);
            }
            return new LoginResponse(false, "Sai mật khẩu nhân viên", null, null);
        }

        Optional<Customer> customerOpt = customerRepo.findByEmailAndPassword(username, password);
        if (customerOpt.isPresent()) {
            return new LoginResponse(true, "Đăng nhập khách hàng thành công", "TOKEN_CUSTOMER", customerOpt.get());
        }

        return new LoginResponse(false, "Không tìm thấy tài khoản phù hợp", null, null);
    }
}