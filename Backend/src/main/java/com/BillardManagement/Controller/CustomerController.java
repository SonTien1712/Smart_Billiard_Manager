package com.BillardManagement.Controller;

import com.BillardManagement.Entity.Billardclub;
import com.BillardManagement.Entity.Customer;
import com.BillardManagement.Service.BilliardClubService;
import com.BillardManagement.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000") // Cho phép React frontend truy cập
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private BilliardClubService billiardClubService;

    // Lấy tất cả khách hàng
    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return customerService.getCustomerById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Customer not found")));
    }

    // Lấy danh sách clubs của 1 customer
    @GetMapping("/{id}/clubs")
    public ResponseEntity<?> getClubsByCustomer(@PathVariable Integer id) {
        List<Billardclub> clubs = billiardClubService.getClubsByCustomerId(id);
        return ResponseEntity.ok(clubs);
    }
}