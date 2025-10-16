package com.BillardManagement.DTO.Request;

import lombok.Data;

/** Tùy chọn: FE của bạn hiện đang POST không body,
 *  nên field này là optional để dùng khi bạn muốn gửi kèm refreshToken. */
@Data
public class LogoutRequest {
    private String refreshToken; // optional
}