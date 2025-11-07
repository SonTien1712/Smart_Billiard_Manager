package com.BillardManagement.Service;

import com.BillardManagement.Entity.Employee;

import java.util.List;

public interface EmployeeService {
    List<Employee> findAll();
    Employee findById(Long id);
    Employee create(Employee employee);
    Employee update(Long id, Employee employee);
    void delete(Long id);

    Employee updateProfile(String email, String name, String phone);
}
