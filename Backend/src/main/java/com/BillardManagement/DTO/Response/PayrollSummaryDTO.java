package com.BillardManagement.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PayrollSummaryDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalHours; // sum of HoursWorked
    private BigDecimal hourlyRate; // from Employee
    private BigDecimal totalPay;   // computed per rules

    private Integer totalShifts;   // total công (present/completed)
    private Integer nightShifts;   // số công đêm
    private String employmentType; // Full/Part time
    private BigDecimal baseSalary; // monthly salary (for full time)
    private BigDecimal nightBonus; // nightShifts * 20000
}
