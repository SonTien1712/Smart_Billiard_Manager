package com.BillardManagement.Repository;

import com.BillardManagement.Entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DashboardRepository extends JpaRepository<Bill, Integer> {

    // ==================== ✅ REVENUE QUERY ====================
    @Query(value =
            "SELECT DATE_FORMAT(b.CreatedDate, '%Y-%m') AS month, " +
                    "       COALESCE(SUM(b.FinalAmount), 0) AS revenue " +
                    "FROM Bills b " +
                    "WHERE b.ClubID = :clubId " +
                    "  AND b.CustomerID = :customerId " +
                    "  AND b.CreatedDate >= :fromDate " +
                    "  AND b.CreatedDate < :toDate " +
                    "  AND b.BillStatus = 'Paid' " +
                    "GROUP BY DATE_FORMAT(b.CreatedDate, '%Y-%m') " +
                    "ORDER BY month ASC",
            nativeQuery = true)
    List<Object[]> getMonthlyRevenueRaw(
            @Param("clubId") Integer clubId,
            @Param("customerId") Integer customerId,
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate
    );

    // ==================== ✅ SALARY QUERY - COMPLETELY REWRITTEN ====================
    /**
     * Subquery approach: Tính lương theo từng nhân viên, sau đó SUM theo tháng
     * - FullTime: Lấy Salary * 1 (mỗi tháng chỉ tính 1 lần)
     * - PartTime: SUM(HourlyRate * HoursWorked)
     */
    @Query(value =
            "SELECT month, SUM(monthlySalary) AS totalSalary " +
                    "FROM ( " +
                    "  SELECT " +
                    "    DATE_FORMAT(s.ShiftDate, '%Y-%m') AS month, " +
                    "    e.EmployeeID, " +
                    "    CASE " +
                    "      WHEN e.EmployeeType = 'FullTime' THEN e.Salary " +
                    "      ELSE SUM(e.HourlyRate * s.HoursWorked) " +
                    "    END AS monthlySalary " +
                    "  FROM EmployeeShifts s " +
                    "  JOIN Employees e ON e.EmployeeID = s.EmployeeID " +
                    "  WHERE s.ClubID = :clubId " +
                    "    AND s.CustomerID = :customerId " +
                    "    AND s.ShiftDate >= :fromDate " +
                    "    AND s.ShiftDate < :toDate " +
                    "    AND s.Status = 'Completed' " +
                    "  GROUP BY " +
                    "    DATE_FORMAT(s.ShiftDate, '%Y-%m'), " +
                    "    e.EmployeeID, " +
                    "    e.EmployeeType, " +
                    "    e.Salary " +
                    ") AS emp_monthly_salary " +
                    "GROUP BY month " +
                    "ORDER BY month ASC",
            nativeQuery = true)
    List<Object[]> getMonthlySalaryRaw(
            @Param("clubId") Integer clubId,
            @Param("customerId") Integer customerId,
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate
    );

    // ==================== ✅ TOP PRODUCTS QUERY ====================
    @Query(value =
            "SELECT p.ProductID, " +
                    "       p.ProductName, " +
                    "       p.Category, " +
                    "       SUM(bd.Quantity) AS qtySold, " +
                    "       (p.Price - p.CostPrice) AS profitPerUnit, " +
                    "       SUM(bd.Quantity * (p.Price - p.CostPrice)) AS totalProfit " +
                    "FROM BillDetails bd " +
                    "JOIN Products p ON p.ProductID = bd.ProductID " +
                    "JOIN Bills b ON b.BillID = bd.BillID " +
                    "WHERE bd.ClubID = :clubId " +
                    "  AND bd.CustomerID = :customerId " +
                    "  AND b.CreatedDate >= :fromDate " +
                    "  AND b.CreatedDate < :toDate " +
                    "  AND b.BillStatus = 'Paid' " +
                    "GROUP BY p.ProductID, p.ProductName, p.Category, p.Price, p.CostPrice " +
                    "ORDER BY totalProfit DESC " +
                    "LIMIT :limit",
            nativeQuery = true)
    List<Object[]> getTopProductsByProfitRaw(
            @Param("clubId") Integer clubId,
            @Param("customerId") Integer customerId,
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate,
            @Param("limit") Integer limit
    );

    // ==================== ✅ EMPLOYEE SALARY DETAILS ====================
    /**
     * Chi tiết lương từng nhân viên
     * Dùng subquery để tính số tháng distinct cho FullTime
     */
    @Query(value =
            "SELECT " +
                    "  e.EmployeeID, " +
                    "  e.EmployeeName, " +
                    "  e.EmployeeType, " +
                    "  CASE " +
                    "    WHEN e.EmployeeType = 'FullTime' THEN e.Salary " +
                    "    ELSE e.HourlyRate " +
                    "  END AS baseSalary, " +
                    "  COALESCE(SUM(s.HoursWorked), 0) AS totalHoursWorked, " +
                    "  COUNT(s.ShiftID) AS totalShifts, " +
                    "  CASE " +
                    "    WHEN e.EmployeeType = 'FullTime' THEN " +
                    "      e.Salary * ( " +
                    "        SELECT COUNT(DISTINCT DATE_FORMAT(ss.ShiftDate, '%Y-%m')) " +
                    "        FROM EmployeeShifts ss " +
                    "        WHERE ss.EmployeeID = e.EmployeeID " +
                    "          AND ss.ShiftDate >= :fromDate " +
                    "          AND ss.ShiftDate < :toDate " +
                    "          AND ss.Status = 'Completed' " +
                    "      ) " +
                    "    ELSE " +
                    "      e.HourlyRate * COALESCE(SUM(s.HoursWorked), 0) " +
                    "  END AS calculatedSalary " +
                    "FROM Employees e " +
                    "LEFT JOIN EmployeeShifts s ON s.EmployeeID = e.EmployeeID " +
                    "  AND s.ShiftDate >= :fromDate " +
                    "  AND s.ShiftDate < :toDate " +
                    "  AND s.Status = 'Completed' " +
                    "WHERE e.ClubID = :clubId " +
                    "  AND e.CustomerID = :customerId " +
                    "  AND e.IsActive = TRUE " +
                    "GROUP BY e.EmployeeID, e.EmployeeName, e.EmployeeType, e.Salary, e.HourlyRate " +
                    "ORDER BY calculatedSalary DESC",
            nativeQuery = true)
    List<Object[]> getEmployeeSalaryDetailsRaw(
            @Param("clubId") Integer clubId,
            @Param("customerId") Integer customerId,
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate
    );
}