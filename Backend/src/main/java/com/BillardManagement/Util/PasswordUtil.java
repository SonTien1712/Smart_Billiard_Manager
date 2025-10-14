package com.BillardManagement.Util;

public class PasswordUtil {

    /**
     * So sánh mật khẩu người dùng nhập với mật khẩu lưu trong cơ sở dữ liệu.
     * @param rawPassword    mật khẩu người dùng nhập
     * @param storedPassword mật khẩu đã lưu trong DB (plain text)
     * @return true nếu khớp, false nếu sai
     */
    public static boolean matches(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) {
            return false;
        }
        return rawPassword.equals(storedPassword);
    }
}