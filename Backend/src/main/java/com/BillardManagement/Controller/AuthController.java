package com.BillardManagement.Controller;

import com.BillardManagement.DTO.Request.LoginRequest;
import com.BillardManagement.DTO.Request.RefreshTokenRequest;
import com.BillardManagement.DTO.Request.RegisterRequest;
import com.BillardManagement.DTO.Response.LoginResponse;
import com.BillardManagement.DTO.Response.LogoutResponse;
import com.BillardManagement.DTO.Response.RefreshTokenResponse;
import com.BillardManagement.DTO.Response.RegisterResponse;
import com.BillardManagement.Entity.Customer;
import com.BillardManagement.Service.AuthService;
import com.BillardManagement.Service.CustomerService;
import com.BillardManagement.Service.ForgotPasswordService;
import com.BillardManagement.Util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;
    private final CustomerService customerService;
    private final ForgotPasswordService forgotPasswordService;
    private final JwtUtil jwtUtil;

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

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();
            if (refreshToken == null || !jwtUtil.isTokenValid(refreshToken)) {
                return ResponseEntity.status(401).body(new RefreshTokenResponse(false, "Invalid refresh token", null, null));
            }

            // Extract claims from refresh token and generate new tokens
            var claims = jwtUtil.extractClaims(refreshToken);
            String username = claims.getSubject();
            String role = (String) claims.get("role");

            Map<String, Object> newClaims = new HashMap<>();
            newClaims.put("sub", username);
            newClaims.put("role", role);
            // Copy other claims if needed
            newClaims.put("userId", claims.get("userId"));
            if (claims.get("employeeId") != null) newClaims.put("employeeId", claims.get("employeeId"));
            if (claims.get("clubId") != null) newClaims.put("clubId", claims.get("clubId"));

            String newAccessToken = jwtUtil.generateAccessToken(newClaims);
            String newRefreshToken = jwtUtil.generateRefreshToken(newClaims);

            return ResponseEntity.ok(new RefreshTokenResponse(true, "Token refreshed successfully", newAccessToken, newRefreshToken));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new RefreshTokenResponse(false, "Token refresh failed", null, null));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            // For now, return a mock profile since different user types have different entities
            // This should be improved to handle different user types (Admin, Customer, Staff)
            return ResponseEntity.ok(Map.of(
                "message", "Profile endpoint - implement based on user type",
                "authenticated", true
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
    }

}

