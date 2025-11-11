package com.BillardManagement.Repository;

import com.BillardManagement.DTO.Response.BilliardTableResponse;
import com.BillardManagement.Entity.Billiardtable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface BilliardTableRepo extends JpaRepository<Billiardtable, Integer> {

    // Tìm theo Club
    List<Billiardtable> findByClubID_Id(Integer clubId);

    // Tìm theo Customer (thông qua Club)
    @Query("SELECT t FROM Billiardtable t WHERE t.clubID.customerID = :customerId")
    List<Billiardtable> findByCustomerID_Id(@Param("customerId") Integer customerId);

    // Tìm và trả về DTO với thông tin Club
    @Query("""
        SELECT new com.BillardManagement.DTO.Response.BilliardTableResponse(
            t.id,
            t.tableName,
            t.tableType,
            t.hourlyRate,
            t.tableStatus,
            t.clubID.id,
            t.clubID.clubName
        )
        FROM Billiardtable t
        WHERE t.clubID.customerID = :customerId
    """)
    List<BilliardTableResponse> findTablesWithClubByCustomerId(@Param("customerId") Integer customerId);

    // Tìm và trả về tất cả DTO
    @Query("""
        SELECT new com.BillardManagement.DTO.Response.BilliardTableResponse(
            t.id,
            t.tableName,
            t.tableType,
            t.hourlyRate,
            t.tableStatus,
            t.clubID.id,
            t.clubID.clubName
        )
        FROM Billiardtable t
    """)
    List<BilliardTableResponse> findAllTablesWithClub();

    // Lock để tránh race condition
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Billiardtable> findLockedById(Integer id);

    // Filter tables by the owning customer via club relation
    List<Billiardtable> findByClubID_CustomerID(Integer customerId);

    /**
     * Đếm số bàn thuộc customer (qua club)
     */
    @Query("SELECT COUNT(t) FROM Billiardtable t WHERE t.clubID.customerID = :customerId")
    Long countByCustomerId(@Param("customerId") Integer customerId);

    @Query("SELECT COUNT(t) FROM Billiardtable t WHERE t.clubID.customerID = :customerId")
    Long countByClubCustomerID(@Param("customerId") Integer customerId);
}