package com.BillardManagement.Repository;
import com.BillardManagement.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepo extends JpaRepository<Employee, Long> {
    Optional<Object> findById(Employee employeeID);
    Optional<Employee> findEmployeeById(Long id);

    @Query("SELECT e FROM Employee e WHERE e.customerID.id = :customerId")
    List<Employee> findByCustomerId(@Param("customerId") Long customerId);
    Optional<Employee> findByEmail(String email);
}
