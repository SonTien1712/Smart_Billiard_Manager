package com.BillardManagement.DTO.Response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class EmployeeResponse {
    private Integer id;
    private String employeeName;
    private String employeeType;
    private String phoneNumber;
    private String email;
    private String address;
    private BigDecimal hourlyRate;
    private BigDecimal salary;

    private Instant dateHired;
    private String bankNumber;
    private String bankName;
    private Boolean isActive;

    // Thông tin mở rộng từ bảng liên kết
    private Integer clubId;
    private String clubName;
    private Integer customerId;
    private String customerName;
}
