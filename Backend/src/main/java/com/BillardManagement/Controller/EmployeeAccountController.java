package com.BillardManagement.Controller;

import com.BillardManagement.DTO.Request.EmployeeAccountRequest;
import com.BillardManagement.DTO.Request.StatusUpdateRequest;
import com.BillardManagement.DTO.Response.EmployeeAccountResponse;
import com.BillardManagement.Service.EmployeeaccountService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid; // Import để sử dụng @Valid
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/staff-accounts") // Đây là đường dẫn API cơ sở
@RequiredArgsConstructor
public class EmployeeAccountController {

    // Tiêm (inject) service mà chúng ta đã tạo
    private final EmployeeaccountService employeeaccountService;

    /**
     * API Endpoint để TẠO MỚI một tài khoản nhân viên
     * HTTP Method: POST
     * Path: /api/v1/employee-accounts
     */
    @PostMapping
    public ResponseEntity<EmployeeAccountResponse> createAccount(
            @Valid @RequestBody EmployeeAccountRequest request) {

        EmployeeAccountResponse newAccount = employeeaccountService.createAccount(request);
        // Trả về 201 Created cùng với thông tin tài khoản đã tạo
        return ResponseEntity.status(HttpStatus.CREATED).body(newAccount);
    }

    /**
     * API Endpoint để LẤY MỘT tài khoản nhân viên theo ID
     * HTTP Method: GET
     * Path: /api/v1/employee-accounts/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeAccountResponse> getAccountById(@PathVariable Integer id) {
        EmployeeAccountResponse account = employeeaccountService.getAccountById(id);
        return ResponseEntity.ok(account); // Trả về 200 OK
    }

    /**
     * API Endpoint để LẤY TẤT CẢ tài khoản nhân viên
     * HTTP Method: GET
     * Path: /api/v1/employee-accounts
     */
    @GetMapping
    public ResponseEntity<List<EmployeeAccountResponse>> getAllAccounts() {
        List<EmployeeAccountResponse> accounts = employeeaccountService.getAllAccounts();
        return ResponseEntity.ok(accounts); // Trả về 200 OK
    }

    /**
     * API Endpoint để CẬP NHẬT một tài khoản nhân viên
     * HTTP Method: PUT
     * Path: /api/v1/employee-accounts/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeAccountResponse> updateAccount(
            @PathVariable Integer id,
            @Valid @RequestBody EmployeeAccountRequest request) {

        EmployeeAccountResponse updatedAccount = employeeaccountService.updateAccount(id, request);
        return ResponseEntity.ok(updatedAccount); // Trả về 200 OK
    }

    /**
     * API Endpoint để XÓA (vô hiệu hóa) một tài khoản nhân viên
     * (Sử dụng "xóa mềm" như đã code trong service)
     * HTTP Method: DELETE
     * Path: /api/v1/employee-accounts/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Integer id) {
        employeeaccountService.deleteAccount(id);
        // Trả về 204 No Content - là standard cho việc xóa thành công
        return ResponseEntity.noContent().build();
    }

    // Thêm phương thức (Endpoint) này vào file EmployeeAccountController.java

    /**
     * API Endpoint để LẤY TẤT CẢ tài khoản nhân viên THEO CUSTOMER ID
     * HTTP Method: GET
     * Path: /api/customer/staff-accounts/by-customer/{customerId}
     */
    @GetMapping("/by-customer/{customerId}")
    public ResponseEntity<List<EmployeeAccountResponse>> getAccountsByCustomerId(
            @PathVariable Long customerId) { // Giả sử customerId là Long

        List<EmployeeAccountResponse> accounts = employeeaccountService.getAccountsByCustomerId(customerId);
        return ResponseEntity.ok(accounts);
    }

    @PatchMapping("/{accountId}/status")
    public ResponseEntity<?> toggleAccountStatus(
            @PathVariable Integer accountId,
            @RequestBody StatusUpdateRequest request) { // <-- Hứng JSON { "status": "..." }

        try {
            employeeaccountService.updateAccountStatus(accountId, request.getStatus());
            return ResponseEntity.ok().build(); // Trả về 200 OK nếu thành công
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build(); // Trả về 404 nếu không tìm thấy
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}