package com.BillardManagement.Repository;

import com.BillardManagement.Entity.Employeeaccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeAccountRepo extends JpaRepository<Employeeaccount, Integer> {
    Optional<Employeeaccount> findByUsername(String username);

    // Allow lookup by the linked employee's email for convenience
    Optional<Employeeaccount> findByEmployeeID_Email(String email);
}
