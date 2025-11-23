package com.BillardManagement.Service;

import com.BillardManagement.Repository.DashboardRepository;
import com.BillardManagement.DTO.Dashboard.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final DashboardRepository repo;

    @PersistenceContext
    private EntityManager em;

    public DashboardService(DashboardRepository repo) {
        this.repo = repo;
    }

    private String toSqlDateStart(LocalDate d) { return d.toString() + " 00:00:00"; }
    private String toSqlDateEnd(LocalDate d) { return d.plusDays(1).toString() + " 00:00:00"; }

    public ClubDashboardDTO buildClubDashboard(Integer customerId, Integer clubId, LocalDate from, LocalDate to, int topN) {
        if (to == null) to = LocalDate.now();
        if (from == null) from = to.minusMonths(6).withDayOfMonth(1);

        String fromStr = toSqlDateStart(from);
        String toStr = toSqlDateEnd(to);

        // Revenue
        List<Object[]> revRows = repo.getMonthlyRevenueRaw(clubId, customerId, fromStr, toStr);
        List<MonthlyRevenueDTO> revenueByMonth = new ArrayList<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;
        if (revRows != null) {
            for (Object[] r : revRows) {
                String month = r[0] != null ? r[0].toString() : "";
                BigDecimal rev = r[1] != null ? new BigDecimal(r[1].toString()) : BigDecimal.ZERO;
                revenueByMonth.add(new MonthlyRevenueDTO(month, rev));
                totalRevenue = totalRevenue.add(rev);
            }
        }

        // Salary
        List<Object[]> salRows = repo.getMonthlySalaryRaw(clubId, customerId, fromStr, toStr);
        List<MonthlySalaryDTO> salaryByMonth = new ArrayList<>();
        BigDecimal totalSalary = BigDecimal.ZERO;
        if (salRows != null) {
            for (Object[] r : salRows) {
                String month = r[0] != null ? r[0].toString() : "";
                BigDecimal sal = r[1] != null ? new BigDecimal(r[1].toString()) : BigDecimal.ZERO;
                salaryByMonth.add(new MonthlySalaryDTO(month, sal));
                totalSalary = totalSalary.add(sal);
            }
        }

        // Top products — handle possible driver issue with LIMIT param
        List<Object[]> prodRows;
        try {
            prodRows = repo.getTopProductsByProfitRaw(clubId, customerId, fromStr, toStr, topN);
        } catch (Exception ex) {
            // fallback: run native query without LIMIT and cut in Java using setMaxResults via EntityManager
            String sql = "SELECT p.ProductID, p.ProductName, p.Category, SUM(bd.Quantity) AS qtySold, " +
                    "(p.Price - p.CostPrice) AS profitPerUnit, " +
                    "SUM(bd.Quantity * (p.Price - p.CostPrice)) AS totalProfit " +
                    "FROM BillDetails bd " +
                    "JOIN Products p ON p.ProductID = bd.ProductID " +
                    "JOIN Bills b ON b.BillID = bd.BillID " +
                    "WHERE bd.ClubID = :clubId AND bd.CustomerID = :customerId " +
                    "  AND b.CreatedDate >= :fromDate AND b.CreatedDate < :toDate " +
                    "GROUP BY p.ProductID, p.ProductName, p.Category, p.Price, p.CostPrice " +
                    "ORDER BY totalProfit DESC";
            Query q = em.createNativeQuery(sql);
            q.setParameter("clubId", clubId);
            q.setParameter("customerId", customerId);
            q.setParameter("fromDate", fromStr);
            q.setParameter("toDate", toStr);
            q.setMaxResults(topN);
            @SuppressWarnings("unchecked")
            List<Object[]> rows = q.getResultList();
            prodRows = rows;
        }

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
        dto.setClubName(null); // optionally fill from Billardclub repo
        dto.setRevenueByMonth(revenueByMonth);
        dto.setSalaryByMonth(salaryByMonth);
        dto.setTopProducts(topProducts);
        return dto;
    }

    public List<EmployeeSalaryDetailDTO> getEmployeeSalaryDetails(
            Integer customerId,
            Integer clubId,
            LocalDate from,
            LocalDate to
    ) {
        if (to == null) to = LocalDate.now();
        if (from == null) from = to.withDayOfMonth(1);

        String fromStr = toSqlDateStart(from);
        String toStr = toSqlDateEnd(to);

        List<Object[]> rawData = repo.getEmployeeSalaryDetailsRaw(clubId, customerId, fromStr, toStr);
        List<EmployeeSalaryDetailDTO> result = new ArrayList<>();

        if (rawData != null) {
            for (Object[] row : rawData) {
                try {
                    Integer employeeId = row[0] != null ? ((Number) row[0]).intValue() : null;
                    String employeeName = row[1] != null ? row[1].toString() : "";
                    String employeeType = row[2] != null ? row[2].toString() : "";
                    BigDecimal baseSalary = row[3] != null ? new BigDecimal(row[3].toString()) : BigDecimal.ZERO;
                    Double totalHours = row[4] != null ? ((Number) row[4]).doubleValue() : 0.0;
                    Long totalShifts = row[5] != null ? ((Number) row[5]).longValue() : 0L;
                    BigDecimal calculatedSalary = row[6] != null ? new BigDecimal(row[6].toString()) : BigDecimal.ZERO;

                    result.add(new EmployeeSalaryDetailDTO(
                            employeeId,
                            employeeName,
                            employeeType,
                            baseSalary,
                            totalHours,
                            totalShifts,
                            calculatedSalary
                    ));
                } catch (Exception e) {
                    // Log error và bỏ qua row này
                    System.err.println("Error parsing employee salary row: " + e.getMessage());
                }
            }
        }

        return result;
    }
}
