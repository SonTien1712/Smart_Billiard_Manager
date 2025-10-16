package com.BillardManagement.Repository;

import com.BillardManagement.Entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepo extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByEmailAndPassword(String email, String password);
    // Kiểm tra trùng số điện thoại
    boolean existsByPhoneNumber(String phoneNumber);
<<<<<<< Updated upstream

    // Kiểm tra trùng email
=======
>>>>>>> Stashed changes
    boolean existsByEmail(String email);
}
