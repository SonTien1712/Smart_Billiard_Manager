package com.BillardManagement.Service.Impl;

import com.BillardManagement.DTO.Request.LoginRequest;

import com.BillardManagement.DTO.Response.EmployeeUserView;
import com.BillardManagement.DTO.Response.LoginResponse;
import com.BillardManagement.Entity.Admin;
import com.BillardManagement.Entity.Customer;
import com.BillardManagement.Entity.Employeeaccount;
import com.BillardManagement.Entity.*;
import com.BillardManagement.Repository.AdminRepo;
import com.BillardManagement.Repository.CustomerRepo;
import com.BillardManagement.Repository.EmployeeAccountRepo;

import com.BillardManagement.Repository.EmployeeRepo;
import com.BillardManagement.Service.AuthService;
import com.BillardManagement.Util.JwtUtil;
import com.BillardManagement.Util.PasswordUtil;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AdminRepo adminRepo;
    private final CustomerRepo customerRepo;
    private final EmployeeAccountRepo employeeAccountRepo;
    private final EmployeeRepo employeeRepo;
    private final JwtUtil jwtUtil;

    // Nghiệp vụ đăng nhập (ưu tiên nhân viên)
    // - Nhận identifier (email) + password
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
                Map<String, Object> claims = new HashMap<>();
                claims.put("sub", admin.getEmail());
                claims.put("role", "ADMIN");
                claims.put("userId", admin.getId());

                String accessToken = jwtUtil.generateAccessToken(claims);
                String refreshToken = jwtUtil.generateRefreshToken(claims);

                return new LoginResponse(true, "Đăng nhập admin thành công", accessToken, refreshToken, admin);
            }
            return new LoginResponse(false, "Sai mật khẩu admin", null, null, null);
        }

        Optional<Employeeaccount> empOpt = employeeAccountRepo.findEmployeeaccountByUsernameAndPasswordHash(identifier, password);
        if (empOpt.isPresent()) {
            return loginEmployee(identifier, password);
        }

        Optional<Customer> customerOpt = customerRepo.findByEmailAndPassword(identifier, password);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            Map<String, Object> claims = new HashMap<>();
            claims.put("sub", customer.getEmail());
            claims.put("role", "CUSTOMER");
            claims.put("userId", customer.getId());

            String accessToken = jwtUtil.generateAccessToken(claims);
            String refreshToken = jwtUtil.generateRefreshToken(claims);

            return new LoginResponse(true, "Đăng nhập khách hàng thành công", accessToken, refreshToken, customer);
        }

        return new LoginResponse(false, "Không tìm thấy tài khoản phù hợp", null, null, null);
    }

    @Transactional(readOnly = true)
    public LoginResponse loginEmployee(String username, String password) {
        Optional<Employeeaccount> empOpt = employeeAccountRepo
                .findEmployeeaccountByUsernameAndPasswordHash(username, password);

        Employeeaccount acc = empOpt.orElse(null);
        if (acc == null) {
            return new LoginResponse(false, "Không tìm thấy tài khoản nhân viên", null, null, null);
        }

        Customer owner = acc.getCustomerID();
        if (owner == null) {
            return new LoginResponse(false, "Không xác định được khách hàng sở hữu câu lạc bộ", null, null, null);
        }

        LocalDate expiryDate = owner.getExpiryDate();
        if (expiryDate == null || expiryDate.isBefore(LocalDate.now())) {
            return new LoginResponse(false, "Tài khoản khách hàng đã hết hạn, vui lòng gia hạn trước khi đăng nhập nhân viên", null, null, null);
        }

        Employee emp = acc.getEmployeeID();
        Billardclub club = acc.getClubID();

        long accountId = acc.getId() != null ? acc.getId() : 0L;
        long employeeId = (emp != null && emp.getId() != null) ? emp.getId() : 0L;
        long clubId = (club != null && club.getId() != null) ? club.getId() : 0L;
        String phone = emp != null ? emp.getPhoneNumber() : null;
        String fullName = emp != null ? emp.getEmployeeName() : acc.getUsername();

        EmployeeUserView user = EmployeeUserView.builder()
                .accountId(accountId)
                .employeeId(employeeId)
                .clubId(clubId)
                .username(acc.getUsername())
                .fullName(fullName)
                .email(acc.getUsername())
                .phoneNumber(phone)
                .role("STAFF")
                .build();

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", acc.getUsername());
        claims.put("role", "STAFF");
        claims.put("userId", acc.getId());
        claims.put("employeeId", emp != null ? emp.getId() : null);
        claims.put("clubId", club != null ? club.getId() : null);

        String accessToken = jwtUtil.generateAccessToken(claims);
        String refreshToken = jwtUtil.generateRefreshToken(claims);

        return new LoginResponse(true, "Đăng nhập nhân viên thành công", accessToken, refreshToken, user);
    }



}
