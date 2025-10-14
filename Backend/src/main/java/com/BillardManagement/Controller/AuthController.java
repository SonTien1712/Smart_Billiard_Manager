package com.BillardManagement.Controller;

import com.BillardManagement.DTO.Request.LoginRequest;
import com.BillardManagement.DTO.Response.LoginResponse;
import com.BillardManagement.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}

