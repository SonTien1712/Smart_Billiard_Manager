package com.BillardManagement.Controller;


import com.BillardManagement.Entity.Customer;
import com.BillardManagement.Service.AdminService;
import com.BillardManagement.Service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import static org.springframework.format.annotation.DateTimeFormat.ISO;

import java.time.Instant;
import java.time.YearMonth;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final CustomerService customerService;

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

}
