package com.BillardManagement.Repository;

import com.BillardManagement.DTO.Response.EmployeeUserView;
import com.BillardManagement.Entity.Employee;
import com.BillardManagement.Entity.Employeeaccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

// Nghiệp vụ nhân viên: Truy cập tài khoản nhân viên và ánh xạ sang thông tin người dùng phục vụ đăng nhập/ủy quyền
public interface EmployeeAccountRepo extends JpaRepository<Employeeaccount, Integer> {
    @Query("""
    select new com.BillardManagement.DTO.Response.EmployeeUserView(
        ea.id,
        e.id,
        e.clubID.id,
        ea.username,
        e.employeeName,
        e.email,
        'STAFF'
    )
    from Employeeaccount ea
    left join ea.employeeID e
    where ea.username = :username
    """)
    // Lấy thông tin đăng nhập + nhân viên theo username, trả về view gọn cho frontend
    Optional<EmployeeUserView> findWithEmployeeByUsername(@Param("username") String username);


    // Tìm tài khoản nhân viên theo username
    Optional<Employeeaccount> findByUsername(String username);

    // Tìm theo username + password hash (có thể bỏ nếu luôn dùng PasswordUtil)
    Optional<Employeeaccount> findEmployeeaccountByUsernameAndPasswordHash(String username, String passwordHash);

    @Query("""
    select ea
    from Employeeaccount ea
    left join ea.employeeID e
    where e.email = :email
    """)
    // Tìm tài khoản nhân viên thông qua email của nhân viên
    Optional<Employeeaccount> findByEmployeeEmail(@Param("email") String email);

    @Query("""
    select new com.BillardManagement.DTO.Response.EmployeeUserView(
        ea.id,
        e.id,
        e.clubID.id,
        ea.username,
        e.employeeName,
        e.email,
        'STAFF'
    )
    from Employeeaccount ea
    left join ea.employeeID e
    where e.email = :email
    """)
    // Lấy view rút gọn của nhân viên thông qua email (phục vụ login bằng email)
    Optional<EmployeeUserView> findViewByEmployeeEmail(@Param("email") String email);

}
