package com.BillardManagement.Service.Impl;

import com.BillardManagement.DTO.Request.UpdateCustomerRequest;
import com.BillardManagement.Entity.Customer;
import com.BillardManagement.Exception.ResourceNotFoundException;
import com.BillardManagement.Repository.CustomerRepo;
import com.BillardManagement.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.time.ZoneId;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepo customerRepository;

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> getCustomerById(Integer id) {
        return customerRepository.findById(id);
    }

    @Override
    public boolean registerCustomer(String name, String email, String phone, String address, String rawPassword) {
        if (customerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }
        if (customerRepository.existsByPhoneNumber(phone)) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại");
        }

        Customer c = new Customer();
        c.setCustomerName(name);
        c.setEmail(email);
        c.setPhoneNumber(phone);
        c.setAddress(address);
        c.setDateJoined(Instant.now());
        c.setPassword(rawPassword);
        c.setIsActive(true);

        customerRepository.save(c);
        return true;
    }

    @Override public long countAll() { return customerRepository.count(); }
    @Override public long countActive() { return customerRepository.countByIsActiveTrue(); }
    @Override public long countNewInMonth(YearMonth ym) {
        var zone = ZoneId.systemDefault();
        var start = ym.atDay(1).atStartOfDay(zone).toInstant();
        var end   = ym.atEndOfMonth().atTime(23,59,59).atZone(zone).toInstant();
        return customerRepository.countByDateJoinedBetween(start, end);
    }
    @Override
    public long countJoinedBetween(Instant from, Instant to) {
        return customerRepository.countByDateJoinedBetween(from, to);
    }
    @Override public double growthRateInMonth(YearMonth ym) {
        var prev = ym.minusMonths(1);
        long cur = countNewInMonth(ym);
        long pre = countNewInMonth(prev);
        return pre == 0 ? (cur > 0 ? 100.0 : 0.0) : ((cur - pre) * 100.0 / pre);
    }
    @Override public Page<Customer> findAll(Pageable pageable) { return customerRepository.findAll(pageable); }

    @Override
    public Optional<Customer> updateStatus(Integer id, boolean isActive) {
        return customerRepository.findById(id).map(c -> {
            c.setIsActive(isActive);
            return customerRepository.save(c);
        });
    }

    @Override
    public Optional<Customer> updateCustomer(Integer id, UpdateCustomerRequest req) {
        return customerRepository.findById(id).map(c -> {
            if (req.getName()    != null) c.setCustomerName(req.getName().trim());
            if (req.getEmail()   != null) c.setEmail(req.getEmail().trim());
            if (req.getPhone()   != null) c.setPhoneNumber(req.getPhone().trim());
            if (req.getAddress() != null) c.setAddress(req.getAddress().trim());
            return customerRepository.save(c);
        });
    }

    @Override
    public void updateExpireDate(Integer id, String planId) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        int monthsToAdd = planId.equals("1") ? 3 : 12;  // Gói 1: 3 tháng, Gói 2: 12 tháng
        LocalDate currentExpire = customer.getExpiryDate();
        LocalDate newExpire = (currentExpire != null && currentExpire.isAfter(LocalDate.now()))
                ? currentExpire.plusMonths(monthsToAdd)
                : LocalDate.now().plusMonths(monthsToAdd);
        customer.setExpiryDate(newExpire);
        customerRepository.save(customer);
    }

    @Override
    public Customer getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user");
        }

        String email = authentication.getName();
        if (email == null || email.isEmpty()) {
            throw new RuntimeException("Invalid user email");
        }

        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public Customer updateProfile(String email, String name, String phone) {
        Optional<Customer> customerOpt = customerRepository.findByEmail(email);
        if (customerOpt.isEmpty()) {
            throw new IllegalArgumentException("Customer not found");
        }
        Customer customer = customerOpt.get();
        customer.setEmail(email);
        customer.setCustomerName(name);
        customer.setPhoneNumber(phone);

        return customerRepository.save(customer);
    }
}
