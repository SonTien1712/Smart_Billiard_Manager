package com.BillardManagement.DTO.Dashboard;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class EmployeeSalaryStatsDTO {
    private Integer clubId;
    private String clubName;
    private List<MonthlySalaryDTO> monthlySalaries;
    private BigDecimal totalSalary;

    // getters/setters, constructors
}

