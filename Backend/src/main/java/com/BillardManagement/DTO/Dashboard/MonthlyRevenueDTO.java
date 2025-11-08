package com.BillardManagement.DTO.Dashboard;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MonthlyRevenueDTO {
    private String month; // "yyyy-MM"
    private BigDecimal revenue;

    public MonthlyRevenueDTO() {}
    public MonthlyRevenueDTO(String month, BigDecimal revenue) {
        this.month = month;
        this.revenue = revenue;
    }
    // getters/setters
}
