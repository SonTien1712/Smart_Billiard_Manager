package com.BillardManagement.Service;



import com.BillardManagement.DTO.Dashboard.*;
import com.BillardManagement.Repository.DashboardRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DashboardService: service layer to build dashboard statistics for clubs.
 *
 * NOTE:
 * - Ensure DashboardReportRepository exists and has the expected methods returning List<Object[]>.
 * - Map Object[] -> DTO carefully depending on repository query column order.
 */
@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final DashboardRepository repo;

    public DashboardService(DashboardRepository repo) {
        this.repo = repo;
    }

    private String toSqlDateStart(LocalDate d) {
        return d.toString() + " 00:00:00";
    }

    private String toSqlDateEnd(LocalDate d) {
        // include the entire day by adding one day and using 00:00:00 as exclusive upper bound
        return d.plusDays(1).toString() + " 00:00:00";
    }

    /**
     * Build ClubDashboardDTO for the given club and date range.
     *
     * @param customerId owner id
     * @param clubId club id
     * @param from start date (inclusive). If null, defaults to first day of current month.
     * @param to end date (inclusive). If null, defaults to today.
     * @param topN number of top products to fetch
     * @return ClubDashboardDTO
     */
    public ClubDashboardDTO buildClubDashboard(Integer customerId, Integer clubId,
                                               LocalDate from, LocalDate to, int topN) {
        if (to == null) to = LocalDate.now();
        if (from == null) from = to.withDayOfMonth(1);

        String fromStr = toSqlDateStart(from);
        String toStr = toSqlDateEnd(to);

        // Revenue rows: expected columns [month(String), revenue(Number)]
        List<Object[]> revRows = repo.getMonthlyRevenue(customerId, clubId, fromStr, toStr);
        List<MonthlyRevenueDTO> revenues = new ArrayList<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;
        if (revRows != null) {
            for (Object[] r : revRows) {
                String month = r[0] != null ? r[0].toString() : "";
                BigDecimal rev = r[1] != null ? new BigDecimal(r[1].toString()) : BigDecimal.ZERO;
                revenues.add(new MonthlyRevenueDTO(month, rev));
                totalRevenue = totalRevenue.add(rev);
            }
        }

        // Salary rows: expected columns [month(String), totalSalary(Number)]
        List<Object[]> salRows = repo.getMonthlySalary(customerId, clubId, fromStr, toStr);
        List<MonthlySalaryDTO> salaries = new ArrayList<>();
        BigDecimal totalSalary = BigDecimal.ZERO;
        if (salRows != null) {
            for (Object[] r : salRows) {
                String month = r[0] != null ? r[0].toString() : "";
                BigDecimal sal = r[1] != null ? new BigDecimal(r[1].toString()) : BigDecimal.ZERO;
                salaries.add(new MonthlySalaryDTO(month, sal));
                totalSalary = totalSalary.add(sal);
            }
        }

        // Top products rows: expected columns [productId, productName, category, qtySold, profitPerUnit, totalProfit]
        List<Object[]> prodRows = repo.getTopProductsByProfit(customerId, clubId, fromStr, toStr, topN);
        List<TopProductStatDTO> topProducts = new ArrayList<>();
        if (prodRows != null) {
            for (Object[] r : prodRows) {
                TopProductStatDTO p = new TopProductStatDTO();
                if (r.length > 0 && r[0] != null) p.setProductId(((Number) r[0]).intValue());
                if (r.length > 1) p.setProductName(r[1] != null ? r[1].toString() : null);
                if (r.length > 2) p.setCategory(r[2] != null ? r[2].toString() : null);
                if (r.length > 3 && r[3] != null) p.setQtySold(((Number) r[3]).longValue());
                if (r.length > 4 && r[4] != null) p.setProfitPerUnit(new BigDecimal(r[4].toString()));
                if (r.length > 5 && r[5] != null) p.setTotalProfit(new BigDecimal(r[5].toString()));
                topProducts.add(p);
            }
        }

        ClubDashboardDTO dto = new ClubDashboardDTO();
        dto.setClubId(clubId);
        // optionally set clubName by querying club repo; for now keep as "Club {id}"
        dto.setClubName("Club " + clubId);
        dto.setRevenueByMonth(revenues);
        dto.setSalaryByMonth(salaries);
        dto.setTopProducts(topProducts);
        return dto;
    }
}