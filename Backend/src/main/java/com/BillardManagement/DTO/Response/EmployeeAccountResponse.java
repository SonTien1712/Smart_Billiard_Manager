package com.BillardManagement.DTO.Response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
@Data
public class EmployeeAccountResponse {
    private Integer id;
    private String username;
    private Boolean isActive;
    private Instant lastLogin;
    private Instant createdDate;

    // Thay vì trả về cả object, chúng ta chỉ trả về ID
    // hoặc các DTO đơn giản của các object đó
    private Integer employeeId;
    private String employeeName; // Ví dụ: bạn có thể "flatten" (làm phẳng) dữ liệu

    private Integer clubId;
    private String clubName; // Ví dụ

    private Integer customerId;
    private String customerName; // Ví dụ
}
