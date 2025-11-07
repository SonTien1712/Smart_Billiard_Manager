package com.BillardManagement.DTO.Response;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder // ✅ SỬA LỖI 6: Thêm @Builder
public class DashboardStatsDTO {

    // Các chỉ số KPI Cards
    private BigDecimal todayRevenue;
    private Long todayBills;
    private Long totalTables;
    private Long totalEmployees;
    private Long activeShifts;
    private Long totalProducts;
    private Double monthlyGrowth; // % tăng trưởng so với tháng trước

    // Dữ liệu cho biểu đồ
    private List<RevenueData> revenueData;
    private List<TableUsageData> tableUsageData;

    /**
     * ✅ SỬA LỖI 7: Phải là 'static class' (lớp tĩnh), không phải 'interface'
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RevenueData {
        private String date;
        private BigDecimal revenue;
    }

    /**
     * ✅ SỬA LỖI 7: Phải là 'static class' (lớp tĩnh), không phải 'interface'
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TableUsageData {
        private String table;
        private Double hours;
    }
}