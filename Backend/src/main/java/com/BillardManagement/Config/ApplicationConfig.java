package com.BillardManagement.Config; // (Hoặc package config của bạn)

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationConfig {

    /**
     * Định nghĩa một Bean PasswordEncoder để Spring có thể inject
     * vào các service khác.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Sử dụng BCrypt, một thuật toán băm mạnh và phổ biến
        return new BCryptPasswordEncoder();
    }
}