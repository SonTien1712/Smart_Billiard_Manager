package com.BillardManagement.DTO.Request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class EmployeeRequest {
    private String employeeName;
    private String employeeType;
    private String phoneNumber;
    private String email;
    private String address;
    private BigDecimal hourlyRate; // Part-time
    private BigDecimal salary;     // ✅ Full-time
    private Instant dateHired;
    private String bankNumber;
    private String bankName;
    private Boolean isActive;

    // Các ID tham chiếu (không cần object đầy đủ)
    private Integer clubId;
    private Integer customerId;
}
