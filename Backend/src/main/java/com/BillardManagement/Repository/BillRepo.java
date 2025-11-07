package com.BillardManagement.Repository;

import com.BillardManagement.DTO.Response.DashboardStatsDTO;
import com.BillardManagement.Entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepo extends JpaRepository<Bill, Integer> {

    // ... (Các phương thức cũ của bạn từ dòng 19-48 không đổi) ...
    List<Bill> findTop10ByOrderByCreatedDateDesc();
    List<Bill> findTop10ByBillStatusIgnoreCaseOrderByCreatedDateDesc(String billStatus);
    List<Bill> findTop10ByClubID_IdAndBillStatusIgnoreCaseOrderByCreatedDateDesc(Integer clubId, String billStatus);
    List<Bill> findTop10ByCustomerID_IdAndBillStatusIgnoreCaseOrderByCreatedDateDesc(Integer customerId, String billStatus);
    List<Bill> findTop10ByBillStatusIgnoreCaseOrderByEndTimeDesc(String billStatus);
    List<Bill> findTop10ByClubID_IdAndBillStatusIgnoreCaseOrderByEndTimeDesc(Integer clubId, String billStatus);
    List<Bill> findTop10ByCustomerID_IdAndBillStatusIgnoreCaseOrderByEndTimeDesc(Integer customerId, String billStatus);
    Optional<Bill> findFirstByTableID_IdAndEndTimeIsNullOrderByStartTimeDesc(Integer tableId);
    long countByCreatedDateBetween(Instant start, Instant end);
    long countByEndTimeBetween(Instant start, Instant end);
    List<Bill> findByBillStatusIgnoreCaseAndCreatedDateBetween(String billStatus, Instant start, Instant end);
    List<Bill> findByBillStatusIgnoreCaseAndEndTimeBetween(String billStatus, Instant start, Instant end);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Bill b where b.tableID.id = :tableId and b.endTime is null")
    Optional<Bill> lockActiveBillByTable(@Param("tableId") Integer tableId);

    Optional<Bill> findById(Integer id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Bill b where b.id = :id")
    Optional<Bill> lockById(@Param("id") Integer id);

    @Query("select b from Bill b left join fetch b.tableID where b.id = :id")
    Optional<Bill> findViewById(@Param("id") Integer id);

    // ==================== DASHBOARD QUERIES (Dành cho Customer) ====================
    // (Các truy vấn này đã đúng)

    @Query("SELECT SUM(b.finalAmount) FROM Bill b " +
            "WHERE b.clubID.customerID = :customerId " +
            "AND b.billStatus = 'Paid'")
    Double findTotalRevenueByCustomerId(@Param("customerId") Integer customerId);

    @Query("SELECT SUM(b.finalAmount) FROM Bill b " +
            "WHERE b.clubID.customerID = :customerId " +
            "AND b.billStatus = 'Paid' " +
            "AND b.endTime >= :startDate " +
            "AND b.endTime < :endDate")
    Double findTotalRevenueByCustomerIdAndDateRange(
            @Param("customerId") Integer customerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // ✅ SỬA LỖI 5: Thêm phương thức này để DashboardService (tệp cũ) có thể biên dịch
    @Query("SELECT SUM(b.finalAmount) FROM Bill b " +
            "WHERE b.clubID.customerID = :customerId " +
            "AND b.billStatus = 'Paid' " +
            "AND b.endTime >= :today")
    Double findTodayRevenueByCustomerId(
            @Param("customerId") Integer customerId,
            @Param("today") LocalDateTime today);

    @Query("SELECT " +
            "FUNCTION('DATE_FORMAT', b.endTime, '%Y-%m-%d') AS date, " +
            "COALESCE(SUM(b.finalAmount), 0) AS revenue " +
            "FROM Bill b " +
            "WHERE b.clubID.customerID = :customerId " +
            "AND b.billStatus = 'Paid' " +
            "AND b.endTime >= :startDate " +
            "GROUP BY FUNCTION('DATE_FORMAT', b.endTime, '%Y-%m-%d') " +
            "ORDER BY FUNCTION('DATE_FORMAT', b.endTime, '%Y-%m-%d') ASC")
    List<DashboardStatsDTO.RevenueData> findDailyRevenueByCustomerId(
            @Param("customerId") Integer customerId,
            @Param("startDate") LocalDateTime startDate);

    @Query("SELECT " +
            "t.tableName AS table, " +
            "COALESCE(SUM(b.totalHours), 0.0) AS hours " +
            "FROM Bill b " +
            "JOIN b.tableID t " +
            "WHERE b.clubID.customerID = :customerId " +
            "AND b.endTime >= :today " +
            "AND b.billStatus IN ('Paid', 'Unpaid') " +
            "GROUP BY t.id, t.tableName " +
            "ORDER BY SUM(b.totalHours) DESC")
    List<DashboardStatsDTO.TableUsageData> findTodayTableUsageByCustomerId(
            @Param("customerId") Integer customerId,
            @Param("today") LocalDateTime today);

    @Query("SELECT COUNT(b) FROM Bill b " +
            "WHERE b.clubID.customerID = :customerId " +
            "AND b.startTime >= :today")
    Long countTodayBillsByCustomerId(
            @Param("customerId") Integer customerId,
            @Param("today") LocalDateTime today);

    // ==================== CÁC TRUY VẤN CŨ (TOÀN CỤC) - ĐÃ SỬA LỖI ====================

    @Query("SELECT COALESCE(SUM(b.finalAmount), 0.0) FROM Bill b " +
            "WHERE b.endTime BETWEEN :startDate AND :endDate " +
            "AND b.billStatus = 'Paid'")
    Double findTotalRevenueBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT " +
            "FUNCTION('DATE_FORMAT', b.endTime, '%Y-%m-%d') AS date, " +
            "SUM(b.finalAmount) AS revenue " +
            "FROM Bill b " +
            "WHERE b.endTime >= :startDate AND b.billStatus = 'Paid' " +
            "GROUP BY FUNCTION('DATE_FORMAT', b.endTime, '%Y-%m-%d') " +
            "ORDER BY FUNCTION('DATE_FORMAT', b.endTime, '%Y-%m-%d') ASC")
    List<DashboardStatsDTO.RevenueData> findDailyRevenueSince(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT " +
            "b.tableID.tableName AS table, " +
            "CAST(SUM(FUNCTION('TIME_TO_SEC', FUNCTION('TIMEDIFF', b.endTime, b.startTime))) AS double) / 3600.0 AS hours " +
            "FROM Bill b " +
            "WHERE b.tableID IS NOT NULL AND b.endTime IS NOT NULL AND b.startTime IS NOT NULL " +
            "AND b.endTime BETWEEN :startDate AND :endDate " +
            "GROUP BY b.tableID.tableName")
    List<DashboardStatsDTO.TableUsageData> findTableUsageBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

}