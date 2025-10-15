package com.BillardManagement.Service.Impl;
import com.BillardManagement.Entity.Employee;
import com.BillardManagement.Repository.EmployeeRepo;
import com.BillardManagement.Service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepo employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + id));
    }

    @Override
    public Employee create(Employee employee) {
        // Ví dụ: validate đơn giản, thêm rule riêng nếu cần
        // if (employee.getEmail() != null && employeeRepository.existsByEmail(employee.getEmail())) ...
        return employeeRepository.save(employee);
    }

    @Override
    public Employee update(Long id, Employee incoming) {
        Employee existed = findById(id);
        copyUpdatableFields(incoming, existed);
        return employeeRepository.save(existed);
    }

    @Override
    public void delete(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new IllegalArgumentException("Employee not found: " + id);
        }
        employeeRepository.deleteById(id);
    }

    /**
     * Chỉ copy các field cho phép update. Hãy chỉnh cho khớp với Employee của bạn.
     */
    private void copyUpdatableFields(Employee src, Employee target) {
        // Ví dụ minh họa — đổi theo entity của bạn:
        // if (src.getEmployeeName() != null) target.setEmployeeName(src.getEmployeeName());
        // if (src.getPhone() != null)        target.setPhone(src.getPhone());
        // if (src.getAddress() != null)      target.setAddress(src.getAddress());
        // if (src.getStatus() != null)       target.setStatus(src.getStatus());
    }
}