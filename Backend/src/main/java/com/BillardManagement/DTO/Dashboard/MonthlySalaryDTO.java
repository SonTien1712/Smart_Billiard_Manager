package com.BillardManagement.DTO.Dashboard;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MonthlySalaryDTO {
    private String month;
    private BigDecimal totalSalary;


    // getters/setters, constructor
}

