package com.BillardManagement.DTO.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutRequest {
    private String refreshToken;
    private String deviceId; // optional
}