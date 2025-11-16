package com.BillardManagement.Repository;

import com.BillardManagement.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, Long> {
    // Find employees by club id (Employee.clubID.id)
    List<Employee> findByClubID_Id(Integer clubId);
    Optional<Object> findById(Employee employeeID);
    Optional<Employee> findEmployeeById(Long id);

    @Query("SELECT e FROM Employee e WHERE e.customerID.id = :customerId")
    List<Employee> findByCustomerId(@Param("customerId") Long customerId);
    Optional<Employee> findByEmail(String email);
}
