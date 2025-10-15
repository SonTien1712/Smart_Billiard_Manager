package com.BillardManagement.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import  com.BillardManagement.Entity.Billardclub;;
public interface BilliardClubRepo extends JpaRepository<Billardclub, Integer> {
    // Không cần viết gì thêm — JpaRepository đã có sẵn CRUD:
    // findAll(), findById(), save(), deleteById(), existsById()...
}
