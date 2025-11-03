package com.BillardManagement.Repository;

import com.BillardManagement.Entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface CustomerRepo extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByEmailIgnoreCase(String email);
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByEmailAndPassword(String email, String password);
    // Kiểm tra trùng số điện thoại
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);

    long countByIsActiveTrue();
    long countByDateJoinedBetween(Instant start, Instant end);
}
