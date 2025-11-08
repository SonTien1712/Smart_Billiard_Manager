package com.BillardManagement.Repository;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DashboardRepository extends Repository<Object, Long> {

    @Query(value =
            "SELECT DATE_FORMAT(b.CreatedDate, '%Y-%m') month, COALESCE(SUM(b.FinalAmount),0) revenue " +
                    "FROM Bills b " +
                    "WHERE b.ClubID = :clubId AND b.CustomerID = :customerId AND b.CreatedDate BETWEEN :fromDate AND :toDate " +
                    "GROUP BY month ORDER BY month", nativeQuery = true)
    List<Object[]> getMonthlyRevenue(@Param("customerId") Integer customerId,
                                     @Param("clubId") Integer clubId,
                                     @Param("fromDate") String fromDate,
                                     @Param("toDate") String toDate);

    @Query(value =
            "SELECT DATE_FORMAT(s.ShiftDate, '%Y-%m') month, COALESCE(SUM( " +
                    "  CASE WHEN e.EmployeeType='FullTime' THEN e.Salary ELSE (e.HourlyRate * s.HoursWorked) END),0) totalSalary " +
                    "FROM EmployeeShifts s " +
                    "JOIN Employees e ON e.EmployeeID = s.EmployeeID " +
                    "WHERE s.ClubID = :clubId AND s.CustomerID = :customerId AND s.ShiftDate BETWEEN :fromDate AND :toDate " +
                    "GROUP BY month ORDER BY month", nativeQuery = true)
    List<Object[]> getMonthlySalary(@Param("customerId") Integer customerId,
                                    @Param("clubId") Integer clubId,
                                    @Param("fromDate") String fromDate,
                                    @Param("toDate") String toDate);

    @Query(value =
            "SELECT p.ProductID, p.ProductName, p.Category, SUM(bd.Quantity) qtySold, " +
                    "       (p.Price - p.CostPrice) profitPerUnit, SUM(bd.Quantity * (p.Price - p.CostPrice)) totalProfit " +
                    "FROM BillDetails bd " +
                    "JOIN Products p ON p.ProductID = bd.ProductID " +
                    "JOIN Bills b ON b.BillID = bd.BillID " +
                    "WHERE bd.ClubID = :clubId AND bd.CustomerID = :customerId AND b.CreatedDate BETWEEN :fromDate AND :toDate " +
                    "GROUP BY p.ProductID, p.ProductName, p.Category, p.Price, p.CostPrice " +
                    "ORDER BY totalProfit DESC LIMIT :limit", nativeQuery = true)
    List<Object[]> getTopProductsByProfit(@Param("customerId") Integer customerId,
                                          @Param("clubId") Integer clubId,
                                          @Param("fromDate") String fromDate,
                                          @Param("toDate") String toDate,
                                          @Param("limit") Integer limit);
}

