package com.BillardManagement.Service;

import com.BillardManagement.DTO.Request.EmployeeAccountRequest;
// SỬA Ở ĐÂY: Import đúng Response DTO
import com.BillardManagement.DTO.Response.EmployeeAccountResponse;

import java.util.List;

public interface EmployeeaccountService {
    /**
     * Tạo một tài khoản nhân viên mới
     * @param request DTO chứa thông tin tài khoản
     * @return DTO của tài khoản đã được tạo
     */
    // SỬA Ở ĐÂY: Đổi BilliardTableResponse -> EmployeeAccountResponse
    EmployeeAccountResponse createAccount(EmployeeAccountRequest request);

    /**
     * Lấy thông tin tài khoản bằng ID
     * @param id ID của tài khoản
     * @return DTO của tài khoản
     */
    // SỬA Ở ĐÂY: Đổi BilliardTableResponse -> EmployeeAccountResponse
    EmployeeAccountResponse getAccountById(Integer id);

    /**
     * Lấy danh sách tất cả tài khoản
     * @return List DTO các tài khoản
     */
    // SỬA Ở ĐÂY: Đổi BilliardTableResponse -> EmployeeAccountResponse
    List<EmployeeAccountResponse> getAllAccounts();

    /**
     * Cập nhật thông tin tài khoản (Lưu ý: nên có DTO riêng cho update)
     * @param id ID của tài khoản cần cập nhật
     * @param request DTO chứa thông tin mới
     * @return DTO của tài khoản đã được cập nhật
     */
    // SỬA Ở ĐÂY: Đổi BilliardTableResponse -> EmployeeAccountResponse
    EmployeeAccountResponse updateAccount(Integer id, EmployeeAccountRequest request);

    /**
     * Xóa (hoặc vô hiệu hóa) tài khoản
     * @param id ID của tài khoản cần xóa
     */
    void deleteAccount(Integer id);

    // Thêm phương thức này vào file EmployeeaccountService.java

    /**
     * Lấy danh sách tất cả tài khoản nhân viên thuộc về một Customer
     * @param customerId ID của Customer
     * @return List DTO các tài khoản
     */
    List<EmployeeAccountResponse> getAccountsByCustomerId(Long customerId);

    void updateAccountStatus(Integer accountId, String newStatus);
}