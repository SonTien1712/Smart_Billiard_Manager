package com.BillardManagement.DTO.Dashboard;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter

public class MonthlySalaryDTO {
    private String month;
    private BigDecimal totalSalary;
    public MonthlySalaryDTO() {}
    public MonthlySalaryDTO(String m, BigDecimal s){this.month=m;this.totalSalary=s;}
    public String getMonth(){return month;} public void setMonth(String m){this.month=m;}
    public BigDecimal getTotalSalary(){return totalSalary;} public void setTotalSalary(BigDecimal s){this.totalSalary=s;}
}

