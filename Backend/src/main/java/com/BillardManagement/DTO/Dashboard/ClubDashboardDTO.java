package com.BillardManagement.DTO.Dashboard;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ClubDashboardDTO {
    private Integer clubId;
    private String clubName;
    private List<MonthlyRevenueDTO> revenueByMonth;
    private List<MonthlySalaryDTO> salaryByMonth;
    private List<TopProductStatDTO> topProducts;

    // getters/setters, constructor
}

