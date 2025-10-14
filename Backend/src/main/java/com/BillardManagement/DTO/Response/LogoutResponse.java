package com.BillardManagement.DTO.Response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutResponse {
    private boolean success;
    private String message;

    public LogoutResponse() {}
    public LogoutResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
