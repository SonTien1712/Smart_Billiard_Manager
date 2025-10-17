package com.BillardManagement.Repository;

import com.BillardManagement.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepo extends JpaRepository<Employee, Integer> {
    // Nghiệp vụ nhân viên: Repository dùng JpaRepository
    // - Sử dụng các hàm có sẵn như findById, save, deleteById, findAll
    // - Chưa cần custom query cho Employee ở thời điểm hiện tại
}
