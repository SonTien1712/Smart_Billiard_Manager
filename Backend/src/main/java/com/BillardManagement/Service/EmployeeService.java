package com.BillardManagement.Service;

import com.BillardManagement.Entity.Employee;
import com.BillardManagement.DTO.Request.EmployeeRequest; // Thêm import này
import com.BillardManagement.DTO.Response.EmployeeResponse;


import java.util.List;

public interface EmployeeService {



    List<Employee> findAll();
    Employee findById(Long id);
    Employee create(Employee employee);
    Employee update(Long id, Employee employee);
    void delete(Long id);



    Employee updateProfile(String email, String name, String phone);

    List<Employee> getUnassignedEmployees(Integer customerId);

    // Thêm phương thức này để xử lý logic update
    EmployeeResponse updateEmployee(Integer id, EmployeeRequest request);

    // Thêm phương thức này để xử lý logic create (Tùy chọn, nhưng nên làm)

}
