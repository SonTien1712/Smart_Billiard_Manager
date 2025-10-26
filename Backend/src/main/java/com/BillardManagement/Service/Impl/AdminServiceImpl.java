package com.BillardManagement.Service.Impl;

import com.BillardManagement.Entity.Admin;
import com.BillardManagement.Repository.AdminRepo;
import com.BillardManagement.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepo adminRepo;

    @Autowired
    public AdminServiceImpl(AdminRepo adminRepo) {
        this.adminRepo = adminRepo;
    }

    // 3. Lấy danh sách admin đang active
    @Override
    public List<Admin> getAllActiveAdmins() {
        return adminRepo.findAllByIsActiveTrue();
    }

    // 4. Tạo mới admin
    @Override
    public Admin createAdmin(String username, String email, String rawPassword){
        if (adminRepo.existsByEmail(email)) throw new IllegalArgumentException("Email already exists");

        Admin a = new Admin();
        a.setUsername(username.trim());
        a.setEmail(email.trim());
        a.setPasswordHash(rawPassword);   // ✅ BẮT BUỘC
        a.setIsActive(true);
        a.setCreatedDate(java.time.Instant.now());
        return adminRepo.save(a);
    }

}

