package com.BillardManagement.Service.Impl;

import com.BillardManagement.Service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Override
    public void send(String to, String subject, String htmlBody) {
        MimeMessage msg = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(msg, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(msg);
        } catch (MessagingException e) {
            throw new IllegalStateException("Cannot send email", e);
        }
    }
}