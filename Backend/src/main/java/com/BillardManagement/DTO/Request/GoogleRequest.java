package com.BillardManagement.DTO.Request;
import lombok.Data;

@Data
public class GoogleRequest {
    private String role;   // "CUSTOMER"
    private String code;   // auth code từ FE
}