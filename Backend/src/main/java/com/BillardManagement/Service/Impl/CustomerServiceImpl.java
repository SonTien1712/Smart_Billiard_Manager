// Tệp: Backend/src/main/java/com/BillardManagement/Service/Impl/CustomerServiceImpl.java
package com.BillardManagement.Service.Impl;

import com.BillardManagement.DTO.Request.UpdateCustomerRequest;
import com.BillardManagement.DTO.Response.DashboardStatsDTO;
import com.BillardManagement.Entity.Customer;
import com.BillardManagement.Exception.ResourceNotFoundException;
import com.BillardManagement.Repository.*;
import com.BillardManagement.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepo customerRepository;

    @Autowired
    private BillRepo billRepo;

    @Autowired
    private BilliardTableRepo billiardTableRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private EmployeeshiftRepo employeeshiftRepo;

    // ... (Tất cả các phương thức từ getAllCustomers đến getCurrentUser không đổi) ...

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
        c.setPassword(rawPassword);
        c.setIsActive(true);

        customerRepository.save(c);
        return true;
    }

    @Override
    public long countAll() {
        return customerRepository.count();
    }

    @Override
    public long countActive() {
        return customerRepository.countByIsActiveTrue();
    }

    @Override
    public long countNewInMonth(YearMonth ym) {
        var zone = ZoneId.systemDefault();
        var start = ym.atDay(1).atStartOfDay(zone).toInstant();
        var end = ym.atEndOfMonth().atTime(23, 59, 59).atZone(zone).toInstant();
        return customerRepository.countByDateJoinedBetween(start, end);
    }

    @Override
    public long countJoinedBetween(Instant from, Instant to) {
        return customerRepository.countByDateJoinedBetween(from, to);
    }

    @Override
    public double growthRateInMonth(YearMonth ym) {
        var prev = ym.minusMonths(1);
        long cur = countNewInMonth(ym);
        long pre = countNewInMonth(prev);
        return pre == 0 ? (cur > 0 ? 100.0 : 0.0) : ((cur - pre) * 100.0 / pre);
    }

    @Override
    public Page<Customer> findAll(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

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
            if (req.getName() != null) c.setCustomerName(req.getName().trim());
            if (req.getEmail() != null) c.setEmail(req.getEmail().trim());
            if (req.getPhone() != null) c.setPhoneNumber(req.getPhone().trim());
            if (req.getAddress() != null) c.setAddress(req.getAddress().trim());
            return customerRepository.save(c);
        });
    }

    @Override
    public void updateExpireDate(Integer id, String planId) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        int monthsToAdd = planId.equals("1") ? 3 : 12;
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
    @Transactional(readOnly = true)
    public DashboardStatsDTO getDashboardStats(Integer customerId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfToday = now.toLocalDate().atStartOfDay();
        LocalDateTime startOfSevenDaysAgo = startOfToday.minusDays(7);

        Long totalTables = billiardTableRepo.countByClubCustomerID(customerId);
        Long totalEmployees = employeeRepo.countByClubCustomerID(customerId);
        Long totalProducts = productRepo.countByCustomerId(customerId);
        Long activeShifts = employeeshiftRepo.countActiveShiftsByCustomerId(customerId);

        Double todayRevenueDouble = billRepo.findTotalRevenueByCustomerIdAndDateRange(customerId, startOfToday, now);
        BigDecimal todayRevenue = (todayRevenueDouble != null) ? BigDecimal.valueOf(todayRevenueDouble) : BigDecimal.ZERO;
        Long todayBills = billRepo.countTodayBillsByCustomerId(customerId, startOfToday);

        Double monthlyGrowth = calculateMonthlyGrowth(customerId);

        List<DashboardStatsDTO.RevenueData> rawRevenueData = billRepo.findDailyRevenueByCustomerId(customerId, startOfSevenDaysAgo);
        List<DashboardStatsDTO.RevenueData> revenueData = fillMissingDates(rawRevenueData, 7);

        List<DashboardStatsDTO.TableUsageData> tableUsageData = billRepo.findTodayTableUsageByCustomerId(customerId, startOfToday);

        return DashboardStatsDTO.builder()
                .todayRevenue(todayRevenue)
                .todayBills(todayBills)
                .totalTables(totalTables)
                .totalEmployees(totalEmployees)
                .activeShifts(activeShifts)
                .totalProducts(totalProducts)
                .monthlyGrowth(monthlyGrowth)
                .revenueData(revenueData)
                .tableUsageData(tableUsageData)
                .build();
    }


    public DashboardStatsDTO getDashboardStats() {
        Customer currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new ResourceNotFoundException("Customer not authenticated or not found");
        }
        return getDashboardStats(currentUser.getId());
    }

    private Double calculateMonthlyGrowth(Integer customerId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfThisMonth = now.toLocalDate().withDayOfMonth(1).atStartOfDay();
        LocalDateTime startOfLastMonth = startOfThisMonth.minusMonths(1);

        Double revenueThisMonth = billRepo.findTotalRevenueByCustomerIdAndDateRange(
                customerId, startOfThisMonth, now);

        Double revenueLastMonth = billRepo.findTotalRevenueByCustomerIdAndDateRange(
                customerId, startOfLastMonth, startOfThisMonth);

        double thisMonth = (revenueThisMonth != null) ? revenueThisMonth : 0.0;
        double lastMonth = (revenueLastMonth != null) ? revenueLastMonth : 0.0;

        if (lastMonth == 0.0) {
            return (thisMonth > 0.0) ? 100.0 : 0.0;
        }

        double growth = ((thisMonth - lastMonth) / lastMonth) * 100.0;
        return Math.round(growth * 100.0) / 100.0;
    }

    private List<DashboardStatsDTO.RevenueData> fillMissingDates(
            List<DashboardStatsDTO.RevenueData> data, int days) {

        List<DashboardStatsDTO.RevenueData> result = new ArrayList<>();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String dateStr = date.toString();

            DashboardStatsDTO.RevenueData found = data.stream()
                    .filter(d -> d.getDate().equals(dateStr))
                    .findFirst()
                    .orElse(null);

            if (found != null) {
                result.add(found);
            } else {
                // ✅ SỬA LỖI 7: Phải dùng BigDecimal.ZERO vì RevenueData.revenue là BigDecimal
                result.add(new DashboardStatsDTO.RevenueData(dateStr, BigDecimal.ZERO));
            }
        }

        return result;
    }
}