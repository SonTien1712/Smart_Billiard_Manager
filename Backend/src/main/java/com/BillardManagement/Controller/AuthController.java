package com.BillardManagement.Controller;

import com.BillardManagement.DTO.Request.LoginRequest;
import com.BillardManagement.DTO.Request.LogoutRequest;
import com.BillardManagement.DTO.Response.LoginResponse;
import com.BillardManagement.DTO.Response.LogoutResponse;
import com.BillardManagement.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout() {
        // Không lưu gì, không revoke, chỉ trả OK
        return ResponseEntity.ok(new LogoutResponse(true, "Đăng xuất thành công"));
    }
}

