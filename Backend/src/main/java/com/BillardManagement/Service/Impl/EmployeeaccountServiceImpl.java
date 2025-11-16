package com.BillardManagement.Service.Impl;

import com.BillardManagement.DTO.Request.EmployeeAccountRequest;
import com.BillardManagement.DTO.Response.EmployeeAccountResponse;
import com.BillardManagement.Entity.Billardclub;
import com.BillardManagement.Entity.Customer;
import com.BillardManagement.Entity.Employee;
import com.BillardManagement.Entity.Employeeaccount;
import com.BillardManagement.Repository.BilliardClubRepo;
import com.BillardManagement.Repository.CustomerRepo;
import com.BillardManagement.Repository.EmployeeRepo;
import com.BillardManagement.Repository.EmployeeAccountRepo;
import com.BillardManagement.Service.EmployeeaccountService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder; // Giả sử bạn dùng Spring Security
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional; // Cần import Optional
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Tự động tiêm (inject) các dependency đã khai báo final
public class EmployeeaccountServiceImpl implements EmployeeaccountService {

    // Inject các repository cần thiết
    private final EmployeeAccountRepo employeeaccountRepository;
    private final EmployeeRepo employeeRepository;
    private final BilliardClubRepo billardclubRepository;
    private final CustomerRepo customerRepository;
    private final PasswordEncoder passwordEncoder; // Inject Bean mã hóa mật khẩu

    @Override
    public EmployeeAccountResponse createAccount(EmployeeAccountRequest request) {
        // 1. Kiểm tra username (Giữ nguyên)
        if (employeeaccountRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        // 2. Tìm các thực thể liên quan (Giữ nguyên)
        Employee employee = employeeRepository.findById(Long.valueOf(request.getEmployeeId()))
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + request.getEmployeeId()));

        Billardclub club = billardclubRepository.findById(request.getClubId())
                .orElseThrow(() -> new EntityNotFoundException("Club not found with id: " + request.getClubId()));

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + request.getCustomerId()));

        // 3. Tạo Entity mới (Giữ nguyên)
        Employeeaccount account = new Employeeaccount();
        account.setUsername(request.getUsername());

        // 4. --- SỬA Ở ĐÂY ---
        // Xóa bỏ: account.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        // Thay bằng:
        account.setPasswordHash(request.getPassword()); // Lưu mật khẩu thô

        account.setEmployeeID(employee);
        account.setClubID(club);
        account.setCustomerID(customer);

        account.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        account.setCreatedDate(Instant.now());

        // 5. Lưu vào DB (Giữ nguyên)
        Employeeaccount savedAccount = employeeaccountRepository.save(account);

        // 6. Trả về (Giữ nguyên)
        return convertToResponse(savedAccount);
    }

    @Override
    public EmployeeAccountResponse getAccountById(Integer id) {
        Employeeaccount account = employeeaccountRepository.findById(id)
                // SỬA LOGIC: Chỉ tìm tài khoản còn hoạt động
                // .filter(Employeeaccount::getIsActive) // Bỏ comment nếu muốn
                .orElseThrow(() -> new EntityNotFoundException("Employee account not found with id: " + id));
        return convertToResponse(account);
    }

    @Override
    public List<EmployeeAccountResponse> getAllAccounts() {
        return employeeaccountRepository.findAll()
                .stream()
                // SỬA LOGIC: Chỉ lấy các tài khoản còn hoạt động
                // .filter(Employeeaccount::getIsActive) // Bỏ comment nếu muốn
                .map(this::convertToResponse) // Dùng method reference
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeAccountResponse updateAccount(Integer id, EmployeeAccountRequest request) {
        Employeeaccount existingAccount = employeeaccountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee account not found with id: " + id));

        // **Phần kiểm tra Username (Giữ nguyên)**
        if (request.getUsername() != null && !request.getUsername().isEmpty() && !request.getUsername().equals(existingAccount.getUsername())) {
            Optional<Employeeaccount> accountWithNewUsername = employeeaccountRepository.findByUsername(request.getUsername());
            if (accountWithNewUsername.isPresent()) {
                throw new IllegalArgumentException("Username already exists");
            }
            existingAccount.setUsername(request.getUsername());
        }

        // --- SỬA Ở ĐÂY ---
        // Chỉ cập nhật password nếu nó được cung cấp
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            // Xóa bỏ: existingAccount.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            // Thay bằng:
            existingAccount.setPasswordHash(request.getPassword()); // Lưu mật khẩu thô
        }

        if (request.getIsActive() != null) {
            existingAccount.setIsActive(request.getIsActive());
        }

        // ... (Phần còn lại của hàm giữ nguyên)

        Employeeaccount updatedAccount = employeeaccountRepository.save(existingAccount);
        return convertToResponse(updatedAccount);
    }

    @Override
    public void deleteAccount(Integer id) {
        // **SỬA LOGIC: DÙNG "XÓA MỀM" (SOFT DELETE)**
        Employeeaccount account = employeeaccountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee account not found with id: " + id));

        account.setIsActive(false); // Đánh dấu là không hoạt động
        employeeaccountRepository.save(account);
    }

    // ----- Helper Method (Private) -----

    /**
     * Hàm private để chuyển đổi Entity sang Response DTO
     */
    private EmployeeAccountResponse convertToResponse(Employeeaccount entity) {
        EmployeeAccountResponse response = new EmployeeAccountResponse();
        response.setId(entity.getId());
        response.setUsername(entity.getUsername());
        response.setIsActive(entity.getIsActive());
        response.setLastLogin(entity.getLastLogin());
        response.setCreatedDate(entity.getCreatedDate());

        // **SỬA LOGIC: LẤY THÔNG TIN TÊN**
        if (entity.getEmployeeID() != null) {
            response.setEmployeeId(entity.getEmployeeID().getId());
            // Giả sử Employee có hàm getEmployeeName()
            response.setEmployeeName(entity.getEmployeeID().getEmployeeName());
        }

        if (entity.getClubID() != null) {
            response.setClubId(entity.getClubID().getId());
            // Giả sử Billardclub có hàm getClubName()
            response.setClubName(entity.getClubID().getClubName());
        }

        if (entity.getCustomerID() != null) {
            response.setCustomerId(entity.getCustomerID().getId());
            // Giả sử Customer có hàm getFullName() hoặc tương tự
            response.setCustomerName(entity.getCustomerID().getCustomerName());
        }

        return response;
    }

    // Thêm phương thức này vào file EmployeeaccountServiceImpl.java

    @Override
    public List<EmployeeAccountResponse> getAccountsByCustomerId(Long customerId) {
        // 1. (Rất nên làm) Kiểm tra xem Customer ID này có tồn tại không
        //    Bạn đã tiêm (inject) CustomerRepo rồi nên có thể dùng ngay.
        if (!customerRepository.existsById(Math.toIntExact(customerId))) {
            throw new EntityNotFoundException("Customer not found with id: " + customerId);
        }

        // 2. Gọi phương thức mới từ Repository
        List<Employeeaccount> accounts = employeeaccountRepository.findByCustomerIDId(customerId);

        // 3. Chuyển đổi danh sách Entity sang danh sách Response DTO
        //    Tái sử dụng hàm helper 'convertToResponse' bạn đã viết
        return accounts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void updateAccountStatus(Integer accountId, String newStatus) {
        // 1. Tìm tài khoản
        Employeeaccount account = employeeaccountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản với ID: " + accountId));

        // 2. Cập nhật trạng thái
        // Chuyển string "active" / "inactive" thành boolean
        boolean isActive = "active".equalsIgnoreCase(newStatus);
        account.setIsActive(isActive);

        // 3. Lưu lại vào DB
        employeeaccountRepository.save(account);
    }
}