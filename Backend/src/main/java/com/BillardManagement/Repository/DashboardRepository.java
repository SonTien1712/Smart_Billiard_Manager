package com.BillardManagement.Repository;

import com.BillardManagement.Entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DashboardRepository extends JpaRepository<Bill, Integer> {

    @Query(value =
            "SELECT DATE_FORMAT(b.CreatedDate, '%Y-%m') AS month, COALESCE(SUM(b.FinalAmount),0) AS revenue " +
                    "FROM Bills b " +
                    "WHERE b.ClubID = :clubId AND b.CustomerID = :customerId " +
                    "  AND b.CreatedDate >= :fromDate AND b.CreatedDate < :toDate " +
                    "GROUP BY month " +
                    "ORDER BY month", nativeQuery = true)
    List<Object[]> getMonthlyRevenueRaw(@Param("clubId") Integer clubId,
                                        @Param("customerId") Integer customerId,
                                        @Param("fromDate") String fromDate,
                                        @Param("toDate") String toDate);

    @Query(value =
            "SELECT DATE_FORMAT(s.ShiftDate, '%Y-%m') AS month, " +
                    "       COALESCE(SUM(CASE WHEN e.EmployeeType='FullTime' THEN e.Salary ELSE (e.HourlyRate * s.HoursWorked) END),0) AS totalSalary " +
                    "FROM EmployeeShifts s " +
                    "JOIN Employees e ON e.EmployeeID = s.EmployeeID " +
                    "WHERE s.ClubID = :clubId AND s.CustomerID = :customerId " +
                    "  AND s.ShiftDate >= :fromDate AND s.ShiftDate < :toDate " +
                    "GROUP BY month " +
                    "ORDER BY month", nativeQuery = true)
    List<Object[]> getMonthlySalaryRaw(@Param("clubId") Integer clubId,
                                       @Param("customerId") Integer customerId,
                                       @Param("fromDate") String fromDate,
                                       @Param("toDate") String toDate);

    @Query(value =
            "SELECT p.ProductID, p.ProductName, p.Category, SUM(bd.Quantity) AS qtySold, " +
                    "       (p.Price - p.CostPrice) AS profitPerUnit, " +
                    "       SUM(bd.Quantity * (p.Price - p.CostPrice)) AS totalProfit " +
                    "FROM BillDetails bd " +
                    "JOIN Products p ON p.ProductID = bd.ProductID " +
                    "JOIN Bills b ON b.BillID = bd.BillID " +
                    "WHERE bd.ClubID = :clubId AND bd.CustomerID = :customerId " +
                    "  AND b.CreatedDate >= :fromDate AND b.CreatedDate < :toDate " +
                    "GROUP BY p.ProductID, p.ProductName, p.Category, p.Price, p.CostPrice " +
                    "ORDER BY totalProfit DESC " +
                    "LIMIT :limit", nativeQuery = true)
    List<Object[]> getTopProductsByProfitRaw(@Param("clubId") Integer clubId,
                                             @Param("customerId") Integer customerId,
                                             @Param("fromDate") String fromDate,
                                             @Param("toDate") String toDate,
                                             @Param("limit") Integer limit);
}
