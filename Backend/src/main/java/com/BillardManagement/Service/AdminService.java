package com.BillardManagement.Service;

import com.BillardManagement.Entity.Admin;

import java.util.List;
import java.util.Optional;

public interface AdminService {

    List<Admin> getAllActiveAdmins();

    Admin createAdmin(String username, String email, String rawPassword);

    Admin UpdateProfile(String email, String name, String phone);
}
