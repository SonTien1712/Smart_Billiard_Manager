package com.BillardManagement.DTO.Dashboard;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ClubRevenueStatsDTO {
    private Integer clubId;
    private String clubName;
    private List<MonthlyRevenueDTO> monthlyRevenues; // từ đầu tháng -> list tháng
    private BigDecimal totalRevenue; // tổng trong khoảng

    // getters/setters, constructors
}
