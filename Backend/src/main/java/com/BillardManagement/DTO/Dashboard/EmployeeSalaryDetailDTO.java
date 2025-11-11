package com.BillardManagement.DTO.Dashboard;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO cho chi tiết lương từng nhân viên
 * Dùng để export danh sách nhân viên với thông tin lương
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSalaryDetailDTO {
    private Integer employeeId;
    private String employeeName;
    private String employeeType; // "FullTime" hoặc "PartTime"
    private BigDecimal baseSalary; // Salary cho FullTime, HourlyRate cho PartTime
    private Double totalHoursWorked; // Tổng giờ làm trong khoảng thời gian
    private Integer totalShifts; // Số ca làm việc
    private BigDecimal calculatedSalary; // Lương thực tế (Salary hoặc HourlyRate * Hours)

    // Constructor cho query projection
    public EmployeeSalaryDetailDTO(
            Integer employeeId,
            String employeeName,
            String employeeType,
            BigDecimal baseSalary,
            Double totalHoursWorked,
            Long totalShifts,
            BigDecimal calculatedSalary
    ) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.employeeType = employeeType;
        this.baseSalary = baseSalary;
        this.totalHoursWorked = totalHoursWorked;
        this.totalShifts = totalShifts != null ? totalShifts.intValue() : 0;
        this.calculatedSalary = calculatedSalary;
    }
}