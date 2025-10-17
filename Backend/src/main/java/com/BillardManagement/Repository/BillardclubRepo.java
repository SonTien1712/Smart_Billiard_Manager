package com.BillardManagement.Repository;

import com.BillardManagement.Entity.Billardclub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillardclubRepo extends JpaRepository<Billardclub, Integer> {
    // Tìm Billardclub theo tên
    Optional<Billardclub> findByClubName(String clubName);

    // Có thể bổ sung các phương thức hợp lệ theo cột có thật, ví dụ:
    Optional<Billardclub> findByPhoneNumber(String phoneNumber);
}
