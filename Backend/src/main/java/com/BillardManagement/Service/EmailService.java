package com.BillardManagement.Service;

public interface EmailService {
    void send(String to, String subject, String htmlBody);
}
