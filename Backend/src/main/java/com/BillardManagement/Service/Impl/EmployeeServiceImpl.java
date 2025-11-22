package com.BillardManagement.Service.Impl;
import com.BillardManagement.DTO.Request.EmployeeRequest;
import com.BillardManagement.DTO.Response.EmployeeResponse;
import com.BillardManagement.Entity.*;
import com.BillardManagement.Repository.EmployeeRepo;
import com.BillardManagement.Repository.BilliardClubRepo;
import com.BillardManagement.Repository.CustomerRepo;
import com.BillardManagement.Repository.EmployeeAccountRepo;
import com.BillardManagement.Repository.EmployeeshiftRepo;
import com.BillardManagement.Mapper.EmployeeMapper;


import com.BillardManagement.Service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepo employeeRepository;
    private final BilliardClubRepo billiardClubRepo;
    private final CustomerRepo customerRepository;
    private final EmployeeAccountRepo employeeAccountRepo;
    private final EmployeeMapper employeeMapper;
    private final EmployeeshiftRepo employeeshiftRepo;



    @Override
    @Transactional // RẤT QUAN TRỌNG: Đảm bảo tất cả cùng thành công hoặc thất bại
    public EmployeeResponse updateEmployee(Integer id, EmployeeRequest request) {

        // 1. Tìm Employee hiện có
        Employee existingEmployee = employeeRepository.findById(id.longValue()) // ID của bạn là Integer, nhưng findById yêu cầu Long
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với ID = " + id));

        // 2. Lấy Club ID cũ ra để so sánh
        Long oldClubId = Long.valueOf((existingEmployee.getClubID() != null) ? existingEmployee.getClubID().getId() : null);
        Long newClubId = Long.valueOf(request.getClubId());

        // 3. Cập nhật các trường cơ bản (giống hệt code của bạn)
        existingEmployee.setEmployeeName(request.getEmployeeName());
        existingEmployee.setEmployeeType(request.getEmployeeType());
        existingEmployee.setPhoneNumber(request.getPhoneNumber());
        existingEmployee.setEmail(request.getEmail());
        existingEmployee.setAddress(request.getAddress());
        existingEmployee.setDateHired(request.getDateHired());
        existingEmployee.setBankNumber(request.getBankNumber());
        existingEmployee.setBankName(request.getBankName());
        existingEmployee.setIsActive(request.getIsActive());

        // Logic lương
        if ("FullTime".equalsIgnoreCase(request.getEmployeeType())) {
            existingEmployee.setSalary(request.getSalary());
            existingEmployee.setHourlyRate(null);
        } else {
            existingEmployee.setHourlyRate(request.getHourlyRate());
            existingEmployee.setSalary(null);
        }

        // 4. Tìm và gán đối tượng Club và Customer
        Billardclub newClub = null; // Khai báo newClub ở đây
        if (newClubId != null) {
            newClub = billiardClubRepo.findById(Math.toIntExact(newClubId))
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Club với ID = " + newClubId));
            existingEmployee.setClubID(newClub);
        }

        if (request.getCustomerId() != null) {
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Customer với ID = " + request.getCustomerId()));
            existingEmployee.setCustomerID(customer);
        }

        // 5. Lưu Employee (Thao tác 1)
        Employee savedEmployee = employeeRepository.save(existingEmployee);

        // 6. KIỂM TRA VÀ CẬP NHẬT CÁC BẢNG LIÊN QUAN
        boolean clubHasChanged = (newClubId != null && !newClubId.equals(oldClubId)) ||
                (oldClubId != null && !oldClubId.equals(newClubId));

        // Chỉ update các bảng con nếu Club ID thực sự thay đổi
        if (clubHasChanged && newClub != null) {

            // 7. Cập nhật Employeeaccount (Thao tác 2)
            // Dùng phương thức findByEmployeeID_Id ta vừa thêm
            Employeeaccount account = employeeAccountRepo.findByEmployeeID_Id(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Employeeaccount cho Employee ID: " + id));

            account.setClubID(newClub); // Gán đối tượng Club mới
            employeeAccountRepo.save(account);

            // 8. Cập nhật TẤT CẢ Employeeshift (Thao tác 3)
            // Dùng phương thức findByEmployeeID_Id có sẵn trong EmployeeshiftRepo
            List<Employeeshift> shifts = employeeshiftRepo.findByEmployeeID_Id(id);
            if (!shifts.isEmpty()) {
                for (Employeeshift shift : shifts) {
                    shift.setClubID(newClub); // Gán đối tượng Club mới
                }
                employeeshiftRepo.saveAll(shifts); // Lưu tất cả các shift đã thay đổi
            }
        }

        // 9. Trả về Response
        // Nếu mọi thứ ở trên chạy suôn sẻ, @Transactional sẽ commit.
        // Nếu có lỗi (ví dụ: không tìm thấy Employeeaccount), toàn bộ giao dịch sẽ bị HỦY BỎ (rollback).
        return employeeMapper.toResponse(savedEmployee);
    }




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

    @Override
    public Employee updateProfile(String email, String name, String phone) {
        Optional<Employee> employOpt = employeeRepository.findByEmail(email);
        if (employOpt.isEmpty()) {
            throw new IllegalArgumentException("Employee not found");
        }
        Employee employee = employOpt.get();
        employee.setEmail(email);
        employee.setEmployeeName(name);
        employee.setPhoneNumber(phone);
        return employeeRepository.save(employee);
    }

    @Override
    public List<Employee> getUnassignedEmployees(Integer customerId) {
        // Gọi thẳng hàm repository
        return employeeRepository.findByCustomerID_IdAndAccountIsNull(customerId);
    }
}