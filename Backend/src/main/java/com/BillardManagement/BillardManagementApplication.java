package com.BillardManagement;

import com.BillardManagement.DTO.Request.LoginRequest;
import com.BillardManagement.DTO.Response.LoginResponse;
import com.BillardManagement.Entity.Admin;
import com.BillardManagement.Service.AdminService;
import com.BillardManagement.Service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

// đổi import cho đúng dự án của bạn
import com.BillardManagement.Service.GoogleService;
import com.BillardManagement.DTO.Request.GoogleRequest;
import com.BillardManagement.DTO.Response.LoginResponse;

@SpringBootApplication
public class BillardManagementApplication implements CommandLineRunner {

    private final GoogleService googleService;
    private final ObjectMapper objectMapper;

    public BillardManagementApplication(GoogleService googleService, ObjectMapper objectMapper) {
        this.googleService = googleService;
        this.objectMapper = objectMapper;
    }

    public static void main(String[] args) {
        SpringApplication.run(BillardManagementApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // === TEST GOOGLE AUTH TRONG CONSOLE ===
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Test Google Auth (code flow hoặc idToken) ===");
        System.out.print("Nhập role (vd: CUSTOMER): ");
        String role = sc.nextLine().trim();

        System.out.print("Nhập authorization code (bỏ trống nếu dùng idToken): ");
        String code = sc.nextLine().trim();

        System.out.print("Nhập idToken (bỏ trống nếu đã nhập code): ");
        String idToken = sc.nextLine().trim();

        GoogleRequest req = new GoogleRequest();
        req.setRole(role);
        if (!code.isEmpty())    req.setCode(code);        // field phải trùng DTO
        if (!idToken.isEmpty()) req.setCode(idToken);

        try {
            LoginResponse resp = googleService.handleGoogleAuth(req); // đổi tên hàm nếu khác
            String pretty = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resp);
            System.out.println("\n=== RESPONSE ===");
            System.out.println(pretty);
        } catch (Exception ex) {
            System.err.println("\n=== ERROR ===");
            ex.printStackTrace();
        }

        // Nếu chỉ chạy test one-shot, có thể thoát app:
        // SpringApplication.exit(SpringApplication.run(BillardManagementApplication.class));
    }
}