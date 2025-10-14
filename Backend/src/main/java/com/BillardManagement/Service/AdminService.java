package com.BillardManagement.Service;

import com.BillardManagement.Entity.Admin;

import java.util.List;
import java.util.Optional;

public interface AdminService {

    Optional<Admin> login(String username, String passwordHash);

    Optional<Admin> findByEmail(String email);

    List<Admin> getAllActiveAdmins();

    Admin createAdmin(Admin admin);

    Admin updateAdmin(Admin admin);

    void deactivateAdmin(Integer id);
}
