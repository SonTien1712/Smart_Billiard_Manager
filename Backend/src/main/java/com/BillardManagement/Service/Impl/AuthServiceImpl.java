package com.BillardManagement.Service.Impl;

import com.BillardManagement.DTO.Request.LoginRequest;

import com.BillardManagement.DTO.Response.EmployeeUserView;
import com.BillardManagement.DTO.Response.LoginResponse;
import com.BillardManagement.Entity.*;
import com.BillardManagement.Repository.AdminRepo;
import com.BillardManagement.Repository.CustomerRepo;
import com.BillardManagement.Repository.EmployeeAccountRepo;

import com.BillardManagement.Repository.EmployeeRepo;
import com.BillardManagement.Service.AuthService;
import com.BillardManagement.Util.PasswordUtil;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AdminRepo adminRepo;
    private final CustomerRepo customerRepo;
    private final EmployeeAccountRepo employeeAccountRepo;
    private final EmployeeRepo employeeRepo;

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

        Optional<Employeeaccount> empOpt = employeeAccountRepo.findEmployeeaccountByUsernameAndPasswordHash(username, password);
        if (empOpt.isPresent()) {
            return loginEmployee(username, password);
        }

        Optional<Customer> customerOpt = customerRepo.findByEmailAndPassword(username, password);
        if (customerOpt.isPresent()) {
            return new LoginResponse(true, "Đăng nhập khách hàng thành công", "TOKEN_CUSTOMER", customerOpt.get());
        }

        return new LoginResponse(false, "Không tìm thấy tài khoản phù hợp", null, null);
    }

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

}