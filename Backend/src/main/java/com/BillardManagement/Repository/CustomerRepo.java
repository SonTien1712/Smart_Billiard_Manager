package com.BillardManagement.Repository;

import com.BillardManagement.Entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepo extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByEmailAndPassword(String email, String password);
    Optional<Customer> findByGoogleIdOrEmail(String sub, String email);
    // Kiểm tra trùng số điện thoại
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
}
