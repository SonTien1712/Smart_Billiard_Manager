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
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi đăng nhập: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
