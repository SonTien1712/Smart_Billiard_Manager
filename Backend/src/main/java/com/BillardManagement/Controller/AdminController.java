package com.BillardManagement.Controller;


import com.BillardManagement.DTO.Request.UpdateCustomerRequest;
import com.BillardManagement.DTO.Request.UpdateStatusRequest;
import com.BillardManagement.Entity.Billardclub;
import com.BillardManagement.Entity.Customer;
import com.BillardManagement.Service.AdminService;
import com.BillardManagement.Service.BilliardClubService;
import com.BillardManagement.Service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import static org.springframework.format.annotation.DateTimeFormat.ISO;

import java.time.Instant;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final CustomerService customerService;
    private final BilliardClubService billiardClubService;

    @GetMapping("/statistics")
    public Map<String, Object> getStatistics(
    @RequestParam(required = false)
    @DateTimeFormat(iso = ISO.DATE_TIME) Instant from,
    @RequestParam(required = false)
    @DateTimeFormat(iso = ISO.DATE_TIME) Instant to
    )   {
        var ym = YearMonth.now();
        long newCustomers = (from != null && to != null)
                ? customerService.countJoinedBetween(from, to)
                : customerService.countNewInMonth(ym);
        return Map.of(
                "totalCustomers", customerService.countAll(),
                "activeCustomers", customerService.countActive(),
                // FE cần một con số, không phải List
                    "totalAdmins", adminService.getAllActiveAdmins().size(),
                "newCustomersThisMonth", newCustomers,
                "growthRate", customerService.growthRateInMonth(ym)
        );
    }

    @GetMapping("/customers")
    public Page<Customer> getCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateJoined,desc") String sort)
    {
        // An toàn hóa field & direction
        String[] parts = sort.split(",");
        String prop = parts.length > 0 ? parts[0] : "dateJoined";
        Sort.Direction dir = (parts.length > 1 && "asc".equalsIgnoreCase(parts[1]))
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        // Chỉ cho phép sort theo field có thật
        var allowed = java.util.Set.of("dateJoined", "expiryDate", "customerName", "email", "isActive");
        if (!allowed.contains(prop)) prop = "dateJoined";
        return customerService.findAll(PageRequest.of(page, size, Sort.by(dir, prop)));
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return customerService.getCustomerById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Customer not found")));
    }

    // Lấy danh sách clubs của 1 customer
    @GetMapping("/customers/{id}/clubs")
    public ResponseEntity<?> getClubsByCustomer(@PathVariable Integer id) {
        List<Billardclub> clubs = billiardClubService.getClubsByCustomerId(id);
        return ResponseEntity.ok(clubs);
    }

    @PatchMapping("/customers/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Integer id,
            @RequestBody UpdateStatusRequest req) {

        if (req.getIsActive() == null) {
            return ResponseEntity.badRequest().body("isActive is required");
        }
        return customerService.updateStatus(id, req.getIsActive())
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/customers/{id}/update")
    public ResponseEntity<?> updateCustomer(
            @PathVariable Integer id,
            @RequestBody UpdateCustomerRequest req) {

        return customerService.updateCustomer(id, req)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Customer not found")));
    }
}
