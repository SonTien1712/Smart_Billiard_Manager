package com.BillardManagement.Repository;

import com.BillardManagement.Entity.Employee;
import com.BillardManagement.Entity.Employeeaccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeAccountRepo extends JpaRepository<Employeeaccount, Integer> {
    @Query("""
    select new com.BillardManagement.DTO.Response.EmployeeUserView(
        CAST(ea.id as long),
        CAST(e.id as long),
        COALESCE(CAST(ea.clubID as long), CAST(e.clubID as long)),
        ea.username,
        e.employeeName,
        e.email,
        'STAFF'
        )
        from Employeeaccount ea
        left join Employee e on e.id = ea.id
        where ea.username = :username
    """)
    Optional<Employeeaccount> findWithEmployeeByUsername(@Param("username") String username);

    Optional<Employeeaccount> findByUsername(String username);

    Optional<Employeeaccount> findEmployeeaccountByUsernameAndPasswordHash(String username, String passwordHash);

}
