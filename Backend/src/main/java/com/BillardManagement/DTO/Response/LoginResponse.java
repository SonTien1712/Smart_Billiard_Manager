package com.BillardManagement.DTO.Response;

import com.BillardManagement.Entity.Admin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    private boolean success;
    private String message;
    private String accessToken;
    Object user;
}