package com.BillardManagement.Controller;

import com.BillardManagement.Entity.Billardclub;
import com.BillardManagement.Entity.Customer;
import com.BillardManagement.Service.BilliardClubService;
import com.BillardManagement.Service.CustomerService;
import com.BillardManagement.DTO.Response.DashboardStatsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private BilliardClubService billiardClubService;

    /**
     * Lấy tất cả khách hàng (Admin only)
     */
    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    /**
     * Lấy thông tin customer theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return customerService.getCustomerById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Customer not found")));
    }

    /**
     * Lấy danh sách clubs của customer
     */
    @GetMapping("/{id}/clubs")
    public ResponseEntity<?> getClubsByCustomer(@PathVariable Integer id) {
        List<Billardclub> clubs = billiardClubService.getClubsByCustomerId(id);
        return ResponseEntity.ok(clubs);
    }

    /**
     * Lấy thống kê dashboard của customer đang đăng nhập
     * Tự động lấy customerId từ JWT token
     */
    @GetMapping("/dashboard-stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStatistics() {
        try {
            // Lấy thông tin customer từ authentication context
            Customer currentUser = customerService.getCurrentUser();

            // Lấy thống kê dashboard
            DashboardStatsDTO stats = customerService.getDashboardStats(currentUser.getId());

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(null);
        }
    }

    /**
     * Alternative endpoint: Lấy dashboard stats theo customer ID cụ thể
     * (Có thể dùng cho admin hoặc testing)
     */
    @GetMapping("/{id}/dashboard-stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStatisticsByCustomerId(
            @PathVariable Integer id) {
        try {
            DashboardStatsDTO stats = customerService.getDashboardStats(id);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }
}