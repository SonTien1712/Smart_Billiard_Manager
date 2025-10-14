package com.BillardManagement.DTO.Response;

import com.BillardManagement.Entity.Admin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private boolean success;
    private String message;
    private String accessToken; // JWT token nếu có
    Admin user;
}