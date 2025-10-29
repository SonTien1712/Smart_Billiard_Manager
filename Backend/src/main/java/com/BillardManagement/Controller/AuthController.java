package com.BillardManagement.Controller;

import com.BillardManagement.DTO.Request.LoginRequest;
import com.BillardManagement.DTO.Request.RegisterRequest;
import com.BillardManagement.DTO.Response.LoginResponse;
import com.BillardManagement.DTO.Response.LogoutResponse;
import com.BillardManagement.DTO.Response.RegisterResponse;
import com.BillardManagement.Service.AuthService;
import com.BillardManagement.Service.CustomerService;
import com.BillardManagement.Service.ForgotPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;
    private final CustomerService customerService;
    private final ForgotPasswordService forgotPasswordService;

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

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody @jakarta.validation.Valid com.BillardManagement.DTO.Request.ForgotPasswordRequest req) {
        try {
            forgotPasswordService.requestReset(req.getEmail());
            return ResponseEntity.ok(java.util.Map.of("success", true, "message", "Đã gửi email đặt lại mật khẩu"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/verify-reset-token")
    public ResponseEntity<?> verifyResetToken(@RequestBody @jakarta.validation.Valid com.BillardManagement.DTO.Request.VerifyResetTokenRequest req) {
        try {
            boolean valid = forgotPasswordService.verifyToken(req.getToken());
            return ResponseEntity.ok(java.util.Map.of("valid", valid));
        } catch (IllegalArgumentException e) {
            // KHÔNG để rơi ra 500
            return ResponseEntity.badRequest().body(java.util.Map.of(
                    "valid", false,
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @jakarta.validation.Valid com.BillardManagement.DTO.Request.ResetPasswordRequest req) {
        try {
            forgotPasswordService.resetPassword(req.getToken(), req.getNewPassword());
            return ResponseEntity.ok(java.util.Map.of("success", true, "message", "Đặt lại mật khẩu thành công"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("success", false, "message", e.getMessage()));
        }
    }
}

