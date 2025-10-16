package com.BillardManagement.DTO.Request;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class RegisterRequest {

    private String name;

    private String email;

    private String phone;

    private String address;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
