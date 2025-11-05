package com.BillardManagement.Service;

public interface ForgotPasswordService {
    void requestReset(String email);
    boolean verifyToken(String token);
    void resetPassword(String token, String newPassword);
}
