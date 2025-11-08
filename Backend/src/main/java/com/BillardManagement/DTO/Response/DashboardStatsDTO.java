package com.BillardManagement.DTO.Response;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardStatsDTO {


    private BigDecimal todayRevenue;
    private Long todayBills;
    private Long totalTables;
    private Long totalEmployees;
    private Long activeShifts;
    private Long totalProducts;
    private Double monthlyGrowth;

    // Dữ liệu cho biểu đồ
    private List<RevenueData> revenueData;
    private List<TableUsageData> tableUsageData;


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RevenueData {
        private String date;
        private BigDecimal revenue;
    }

    
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TableUsageData {
        private String table;
        private Double hours;
    }
}