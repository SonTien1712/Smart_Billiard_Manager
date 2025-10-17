package com.BillardManagement.Repository;

import com.BillardManagement.Entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepo extends JpaRepository<Bill, Integer> {
    List<Bill> findTop10ByOrderByCreatedDateDesc();

    List<Bill> findTop10ByBillStatusIgnoreCaseOrderByCreatedDateDesc(String billStatus);

    List<Bill> findTop10ByClubID_IdAndBillStatusIgnoreCaseOrderByCreatedDateDesc(Integer clubId, String billStatus);

    List<Bill> findTop10ByCustomerID_IdAndBillStatusIgnoreCaseOrderByCreatedDateDesc(Integer customerId, String billStatus);

    // For "recently completed" views, prefer EndTime ordering for Paid bills
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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Bill> findById(Integer id);

    // Read-only view without lock, with table join to safely read table name
    @Query("select b from Bill b left join fetch b.tableID where b.id = :id")
    Optional<Bill> findViewById(@Param("id") Integer id);
}
