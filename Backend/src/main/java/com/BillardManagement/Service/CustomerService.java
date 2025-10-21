package com.BillardManagement.Service;
import com.BillardManagement.Entity.Customer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CustomerService {
    List<Customer> getAllCustomers();

    Optional<Customer> getCustomerById(Integer id);

    Customer createCustomer(Customer customer);

    Customer updateCustomer(Integer id, Customer customer);

    void deleteCustomer(Integer id);

    boolean registerCustomer(String name, String email, String phone, String address, String rawPassword);

    Customer upsertGoogleUser(String sub, String email, String name, boolean emailVerified);
}
