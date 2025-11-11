package com.BillardManagement.Repository;

import com.BillardManagement.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, Long> {
    // Find employees by club id (Employee.clubID.id)
    List<Employee> findByClubID_Id(Integer clubId);
}
