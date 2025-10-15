package com.BillardManagement.Repository;

import com.BillardManagement.Entity.Employeeshift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmployeeshiftRepo extends JpaRepository<Employeeshift, Integer> {
    List<Employeeshift> findByEmployeeID_Id(Integer employeeId);

    List<Employeeshift> findByEmployeeID_IdAndShiftDateBetween(Integer employeeId, LocalDate startDate, LocalDate endDate);
}

