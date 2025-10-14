package com.BillardManagement.Repository;

import com.BillardManagement.Entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepo extends JpaRepository<Admin, Integer> {

    // 1. Tìm Admin theo email + password
    Optional<Admin> findByEmailAndPasswordHash(String username, String passwordHash);

    // 2. Tìm Admin theo email
    Optional<Admin> findByEmail(String email);

    // 3. Lấy tất cả Admin đang active
    List<Admin> findAllByIsActiveTrue();

    // 4. Tạo mới Admin => dùng save(admin)

    // 5. Update Admin => dùng save(admin)

    // 6. Xóa mềm (update IsActive = false)
    @Modifying
    @Transactional
    @Query("UPDATE Admin a SET a.isActive = false WHERE a.id = :id")
    void deactivateAdmin(@Param("id") Integer id);

}
