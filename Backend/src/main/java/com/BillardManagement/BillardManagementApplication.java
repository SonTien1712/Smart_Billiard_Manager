package com.BillardManagement;

import com.BillardManagement.DTO.Request.LoginRequest;
import com.BillardManagement.DTO.Response.LoginResponse;
import com.BillardManagement.Entity.Admin;
import com.BillardManagement.Service.AdminService;
import com.BillardManagement.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@SpringBootApplication
public class BillardManagementApplication implements CommandLineRunner {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AuthService authService; // Tự động inject AuthServiceImpl

    public static void main(String[] args) {
        SpringApplication.run(BillardManagementApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("=== 🔐 Kiểm tra đăng nhập thủ công ===");

        try {
            // Tạo request đăng nhập
            LoginRequest request = new LoginRequest();
            request.setEmail("admin@billard.vn");
            request.setPassword("1");

            // Gọi service login
            LoginResponse response = authService.login(request);

            // In kết quả ra console
            System.out.println("✅ Kết quả đăng nhập:");
            System.out.println("Success: " + response.isSuccess());
            System.out.println("Message: " + response.getMessage());
            System.out.println("Token: " + response.getAccessToken());

            // ---- Test CUSTOMER ----
            LoginRequest customerReq = new LoginRequest();
            customerReq.setEmail("nguyenvana@gmail.com");      // email của customer (cột Customers.Email)
            customerReq.setPassword("1");
            LoginResponse customerRes = authService.login(customerReq);

            System.out.println("✅ Kết quả đăng nhập (customer):");
            System.out.println("Success: " + customerRes.isSuccess());
            System.out.println("Message: " + customerRes.getMessage());
            System.out.println("Token: " + customerRes.getAccessToken());

// ---- Test EMPLOYEE ----
// CHÚ Ý: với employee, hệ thống dùng Username (không phải email).
// TẠM THỜI truyền username vào field email của LoginRequest như cách service đang làm.
            LoginRequest empReq = new LoginRequest();
            empReq.setEmail("tuananh@gmail.com");                    // username của nhân viên (cột EmployeeAccounts.Username)
            empReq.setPassword("1");
            LoginResponse empRes = authService.login(empReq);

            System.out.println("✅ Kết quả đăng nhập (employee):");
            System.out.println("Success: " + empRes.isSuccess());
            System.out.println("Message: " + empRes.getMessage());
            System.out.println("Token: " + empRes.getAccessToken());

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi đăng nhập: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
