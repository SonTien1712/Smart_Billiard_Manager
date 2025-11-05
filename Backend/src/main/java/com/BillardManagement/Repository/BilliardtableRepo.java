package com.BillardManagement.Repository;

import com.BillardManagement.Entity.Billiardtable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BilliardtableRepo extends JpaRepository<Billiardtable, Integer> {
    List<Billiardtable> findByClubID_Id(Integer clubId);
    // Filter tables by the owning customer via club relation
    List<Billiardtable> findByClubID_CustomerID(Integer customerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Billiardtable> findLockedById(Integer id);
}
