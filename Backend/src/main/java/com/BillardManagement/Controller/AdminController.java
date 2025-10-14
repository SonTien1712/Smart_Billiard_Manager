package com.BillardManagement.Controller;

import com.BillardManagement.Entity.Admin;
import com.BillardManagement.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/admins")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // 1. Tạo Admin mới
    @PostMapping
    public ResponseEntity<Admin> createAdmin(@RequestBody Admin admin) {
        admin.setCreatedDate(Instant.now());
        admin.setIsActive(true);
        return ResponseEntity.ok(adminService.createAdmin(admin));
    }

    // 2. Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username,
                                   @RequestParam String password) {
        Optional<Admin> admin = adminService.login(username, password);
        return admin.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(401).body("Sai username hoặc password"));
    }

    // 3. Tìm theo Email
    @GetMapping("/email/{email}")
    public ResponseEntity<?> findByEmail(@PathVariable String email) {
        Optional<Admin> admin = adminService.findByEmail(email);
        return admin.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 4. Lấy danh sách Admin active
    @GetMapping
    public ResponseEntity<List<Admin>> getAllActiveAdmins() {
        return ResponseEntity.ok(adminService.getAllActiveAdmins());
    }

    // 5. Update Admin
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAdmin(@PathVariable Integer id, @RequestBody Admin admin) {
        admin.setId(id);
        return ResponseEntity.ok(adminService.updateAdmin(admin));
    }

    // 6. Deactivate Admin
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deactivateAdmin(@PathVariable Integer id) {
        adminService.deactivateAdmin(id);
        return ResponseEntity.ok("Admin đã deactivate ID: " + id);
    }
}
