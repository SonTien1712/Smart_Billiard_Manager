package com.BillardManagement.DTO.Request;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class VerifyResetTokenRequest {
    @NotBlank
    private String token;
}
