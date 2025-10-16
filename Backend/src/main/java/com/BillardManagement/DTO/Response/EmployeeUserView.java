package com.BillardManagement.DTO.Response;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class EmployeeUserView {
    private Long accountId;     // id bảng Employeeaccount (nếu có)
    private Long employeeId;    // id bảng Employee
    private Long clubId;        // id BillardClub (nếu cần)
    private String username;
    private String fullName;    // nếu có cột name trong Employee
    private String email;       // nếu có
    private String role;        // "STAFF"
}