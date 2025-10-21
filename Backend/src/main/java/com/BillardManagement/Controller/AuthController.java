package com.BillardManagement.Controller;

import com.BillardManagement.DTO.Request.LoginRequest;
import com.BillardManagement.DTO.Request.RegisterRequest;
import com.BillardManagement.DTO.Response.LoginResponse;
import com.BillardManagement.DTO.Response.LogoutResponse;
import com.BillardManagement.DTO.Response.RegisterResponse;
import com.BillardManagement.Service.AuthService;
import com.BillardManagement.Service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import com.BillardManagement.DTO.Request.GoogleRequest;
import org.springframework.web.bind.annotation.*;
import com.BillardManagement.Service.GoogleService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;
    private final CustomerService customerService;
    private final GoogleService GoogleService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout() {
        // Không lưu gì, không revoke, chỉ trả OK
        return ResponseEntity.ok(new LogoutResponse(true, "Đăng xuất thành công"));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest req) {
        try {
            boolean ok = customerService.registerCustomer(
                    req.getName(),
                    req.getEmail(),
                    req.getPhone(),
                    req.getAddress(),
                    req.getPassword()
            );

            if (ok) {
                return ResponseEntity.ok(new RegisterResponse(true, "Đăng ký thành công"));
            }
            return ResponseEntity.internalServerError()
                    .body(new RegisterResponse(false, "Đăng ký thất bại"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RegisterResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new RegisterResponse(false, "Lỗi hệ thống"));
        }
    }

    @PostMapping("/google")
    public ResponseEntity<LoginResponse> googleAuth(@RequestBody GoogleRequest req) {
        return ResponseEntity.ok(GoogleService.handleGoogleAuth(req));
    }
}

