package com.BillardManagement.Repository;

import com.BillardManagement.Entity.Employeeshift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmployeeshiftRepo extends JpaRepository<Employeeshift, Integer> {
    List<Employeeshift> findByEmployeeID_Id(Integer employeeId);

    List<Employeeshift> findByEmployeeID_IdAndShiftDateBetween(
            Integer employeeId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Employeeshift> findByClubID_IdAndShiftDateBetween(Integer clubId, LocalDate startDate, LocalDate endDate);

    /**
     * Đếm số ca đang hoạt động (đã check-in nhưng chưa check-out)
     * Thông qua club để filter theo customer
     */
    @Query("SELECT COUNT(s) FROM Employeeshift s " +
            "WHERE s.clubID.customerID = :customerId " +
            "AND s.actualStartTime IS NOT NULL " +
            "AND s.actualEndTime IS NULL")
    Long countActiveShiftsByCustomerId(@Param("customerId") Integer customerId);



    /**
     * ✅ Tìm shifts theo ClubID và CustomerID trong khoảng thời gian
     */
    List<Employeeshift> findByClubID_IdAndCustomerID_IdAndShiftDateBetween(
            Integer clubId,
            Integer customerId,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * ✅ Tìm shifts theo status
     */
    List<Employeeshift> findByEmployeeID_IdAndShiftDateBetweenAndStatus(
            Integer employeeId,
            LocalDate startDate,
            LocalDate endDate,
            String status
    );
}