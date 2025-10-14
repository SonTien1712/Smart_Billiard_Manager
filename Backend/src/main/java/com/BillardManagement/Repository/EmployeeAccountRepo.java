package com.BillardManagement.Repository;

import com.BillardManagement.Entity.Employeeaccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeAccountRepo extends JpaRepository<Employeeaccount, Integer> {
    Optional<Employeeaccount> findByUsername(String username);

    Optional<Employeeaccount> findEmployeeaccountByUsernameAndPasswordHash(String username, String passwordHash);
}
