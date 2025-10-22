package com.BillardManagement.Service;
import com.BillardManagement.Entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<Customer> getAllCustomers();

    Optional<Customer> getCustomerById(Integer id);

    Customer createCustomer(Customer customer);

    Customer updateCustomer(Integer id, Customer customer);

    void deleteCustomer(Integer id);

    boolean registerCustomer(String name, String email, String phone, String address, String rawPassword);

    long countAll();
    long countActive();
    long countNewInMonth(YearMonth ym);
    double growthRateInMonth(YearMonth ym);
    Page<Customer> findAll(Pageable pageable);
    long countJoinedBetween(Instant from, Instant to);
}
