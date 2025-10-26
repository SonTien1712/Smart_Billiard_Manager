package com.BillardManagement.DTO.Request;
import lombok.Getter; import lombok.Setter;

@Getter @Setter
public class CreateAdminRequest {
    private String username;
    private String email;
    private String password; // plain text từ FE
}