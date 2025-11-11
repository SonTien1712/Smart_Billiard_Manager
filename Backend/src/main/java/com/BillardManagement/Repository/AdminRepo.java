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

    Optional<Admin> findByEmailAndPasswordHash(String username, String passwordHash);
    Optional<Admin> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Admin> findAllByIsActiveTrue();

}
