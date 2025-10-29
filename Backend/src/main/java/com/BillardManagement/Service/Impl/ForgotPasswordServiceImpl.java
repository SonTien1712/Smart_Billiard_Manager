package com.BillardManagement.Service.Impl;

import com.BillardManagement.Entity.Customer;
import com.BillardManagement.Entity.Passwordresettoken;
import com.BillardManagement.Repository.CustomerRepo;
import com.BillardManagement.Repository.PasswordresettokenRepo;
import com.BillardManagement.Service.EmailService;
import com.BillardManagement.Service.ForgotPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    private final CustomerRepo customerRepo;
    private final PasswordresettokenRepo tokenRepo;

    private final EmailService emailService;

    // FE trang reset, vd: http://localhost:3000/reset
    @Value("${app.reset.base-url:http://localhost:3000/reset}")
    private String resetBaseUrl;

    // TTL mặc định: 15 phút
    @Value("${app.reset.ttl-minutes:15}")
    private long ttlMinutes;

    @Override
    public void requestReset(String email) {
        Optional<Customer> userOpt = customerRepo.findByEmailIgnoreCase(email);
        if (userOpt.isEmpty()) {
            return;
        }
        Customer user = userOpt.get();

        String token = UUID.randomUUID().toString();

        Passwordresettoken prt = new Passwordresettoken();
        prt.setToken(token);
        prt.setUserID(user);
        prt.setCreatedDate(Instant.now());
        prt.setExpiryDate(Instant.now().plus(ttlMinutes, ChronoUnit.MINUTES));
        tokenRepo.save(prt);

        String body = """
                        <p>Xin chào %s,</p>
                        <p>Bạn vừa yêu cầu đặt lại mật khẩu. Token của bạn là:</p>
                        <p><strong>%s</strong></p>
                        <p>Token có hiệu lực trong %d phút.</p>
                        <p>Nếu không phải bạn, vui lòng bỏ qua email này.</p>
                      """.formatted(user.getCustomerName(), token, ttlMinutes);

        emailService.send(user.getEmail(), "Đặt lại mật khẩu", body);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifyToken(String token) {
        var t = tokenRepo.findByToken(token).orElse(null);
        if (t == null) return false;

        if (t.getExpiryDate() == null || Instant.now().isAfter(t.getExpiryDate())) {
            // token hết hạn → dọn dẹp và trả false
            tokenRepo.delete(t);
            return false;
        }
        return true;
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        Passwordresettoken t = tokenRepo.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token không hợp lệ"));

        if (t.getExpiryDate() == null || Instant.now().isAfter(t.getExpiryDate())) {
            // hết hạn -> dọn token luôn
            tokenRepo.delete(t);
            throw new IllegalArgumentException("Token đã hết hạn");
        }

        Customer user = t.getUserID();
        user.setPassword(newPassword);
        customerRepo.save(user);
        tokenRepo.delete(t);
    }
}
