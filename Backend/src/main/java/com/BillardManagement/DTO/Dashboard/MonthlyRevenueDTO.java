package com.BillardManagement.DTO.Dashboard;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class MonthlyRevenueDTO {
    private String month;
    private BigDecimal revenue;
    // constructors, getters, setters
    public MonthlyRevenueDTO() {}
    public MonthlyRevenueDTO(String m, BigDecimal r) { this.month=m; this.revenue=r; }
    public String getMonth(){return month;} public void setMonth(String m){this.month=m;}
    public BigDecimal getRevenue(){return revenue;} public void setRevenue(BigDecimal r){this.revenue=r;}
}
    // getters/setters

