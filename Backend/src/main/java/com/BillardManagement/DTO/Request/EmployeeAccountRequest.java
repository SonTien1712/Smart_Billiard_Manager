package com.BillardManagement.DTO.Request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Data
public class EmployeeAccountRequest {
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password; // Nhận mật khẩu thô, chúng ta sẽ hash ở Service

    @NotNull(message = "Employee ID cannot be null")
    private Integer employeeId; // Chỉ nhận ID

    @NotNull(message = "Club ID cannot be null")
    private Integer clubId; // Chỉ nhận ID

    @NotNull(message = "Customer ID cannot be null")
    private Integer customerId; // Chỉ nhận ID

    private Boolean isActive; // Có thể tùy chọn, mặc định ở service
}
