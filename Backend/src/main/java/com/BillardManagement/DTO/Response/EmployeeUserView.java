package com.BillardManagement.DTO.Response;
import lombok.*;

// View rút gọn trả về khi đăng nhập nhân viên
// - Dùng để hiển thị thông tin cơ bản và phân quyền ở FE
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class EmployeeUserView {
    private Integer accountId;   // ID tài khoản (Employeeaccount)
    private Integer employeeId;  // ID nhân viên
    private Integer clubId;      // ID câu lạc bộ làm việc
    private String username;     // tên đăng nhập
    private String fullName;     // tên nhân viên
    private String email;        // email nhân viên
    private String role;         // vai trò: "STAFF"
}
