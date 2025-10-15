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
    }
}
