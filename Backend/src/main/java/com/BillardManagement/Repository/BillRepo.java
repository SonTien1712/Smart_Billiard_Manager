package com.BillardManagement.Repository;

import com.BillardManagement.Entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
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

    Optional<Bill> findFirstByTableID_IdAndEndTimeIsNullOrderByStartTimeDesc(Integer tableId);

    long countByCreatedDateBetween(Instant start, Instant end);

    List<Bill> findByBillStatusIgnoreCaseAndCreatedDateBetween(String billStatus, Instant start, Instant end);

    @Lock(org.springframework.data.jpa.repository.LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Bill b where b.tableID.id = :tableId and b.endTime is null")
    Optional<Bill> lockActiveBillByTable(@Param("tableId") Integer tableId);
}
