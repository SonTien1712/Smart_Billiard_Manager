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

    // 1. Đăng nhập bằng username + password
    @Override
    public Optional<Admin> login(String username, String passwordHash) {
        return adminRepo.findByEmailAndPasswordHash(username, passwordHash);
    }

    // 2. Tìm bằng email
    @Override
    public Optional<Admin> findByEmail(String email) {
        return adminRepo.findByEmail(email);
    }

    // 3. Lấy danh sách admin đang active
    @Override
    public List<Admin> getAllActiveAdmins() {
        return adminRepo.findAllByIsActiveTrue();
    }

    // 4. Tạo mới admin
    @Override
    public Admin createAdmin(Admin admin) {
        return adminRepo.save(admin);
    }

    // 5. Update admin
    @Override
    public Admin updateAdmin(Admin admin) {
        return adminRepo.save(admin);
    }

    // 6. Xóa mềm admin (update IsActive = false)
    @Override
    public void deactivateAdmin(Integer id) {
        adminRepo.deactivateAdmin(id);
    }
}

