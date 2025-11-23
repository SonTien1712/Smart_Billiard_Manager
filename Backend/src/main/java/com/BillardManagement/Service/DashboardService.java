package com.BillardManagement.Service;

import com.BillardManagement.Repository.DashboardRepository;
import com.BillardManagement.Repository.EmployeeshiftRepo;
import com.BillardManagement.Repository.EmployeeRepo;
import com.BillardManagement.Repository.BillRepo;
import com.BillardManagement.Repository.BilldetailRepo;
import com.BillardManagement.Repository.ProductRepository;
import com.BillardManagement.DTO.Dashboard.*;
import com.BillardManagement.Entity.Employeeshift;
import com.BillardManagement.Entity.Employee;
import com.BillardManagement.Entity.Bill;
import com.BillardManagement.Entity.Billdetail;
import com.BillardManagement.Entity.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final DashboardRepository repo;
    private final EmployeeshiftRepo employeeshiftRepo;
    private final EmployeeRepo employeeRepo;
    private final BillRepo billRepo;
    private final BilldetailRepo billdetailRepo;
    private final ProductRepository productRepo;

    @PersistenceContext
    private EntityManager em;

    public DashboardService(
            DashboardRepository repo,
            EmployeeshiftRepo employeeshiftRepo,
            EmployeeRepo employeeRepo,
            BillRepo billRepo,
            BilldetailRepo billdetailRepo,
            ProductRepository productRepo
    ) {
        this.repo = repo;
        this.employeeshiftRepo = employeeshiftRepo;
        this.employeeRepo = employeeRepo;
        this.billRepo = billRepo;
        this.billdetailRepo = billdetailRepo;
        this.productRepo = productRepo;
    }

    private String toSqlDateStart(LocalDate d) {
        return d.toString() + " 00:00:00";
    }

    private String toSqlDateEnd(LocalDate d) {
        return d.plusDays(1).toString() + " 00:00:00";
    }

    /**
     * ✅ Build dashboard với logic cải tiến từ StaffController và StaffBillingController
     */
    public ClubDashboardDTO buildClubDashboard(
            Integer customerId,
            Integer clubId,
            LocalDate from,
            LocalDate to,
            int topN
    ) {
        if (to == null) to = LocalDate.now();
        if (from == null) from = to.minusMonths(6).withDayOfMonth(1);

        // ==================== REVENUE - Dựa trên StaffBillingController ====================
        List<MonthlyRevenueDTO> revenueByMonth = calculateMonthlyRevenue(customerId, clubId, from, to);

        // ==================== SALARY - Dựa trên StaffController logic ====================
        List<MonthlySalaryDTO> salaryByMonth = calculateMonthlySalaryWithStaffLogic(customerId, clubId, from, to);

        // ==================== TOP PRODUCTS - Dựa trên StaffBillingController ====================
        List<TopProductStatDTO> topProducts = calculateTopProducts(customerId, clubId, from, to, topN);

        ClubDashboardDTO dto = new ClubDashboardDTO();
        dto.setClubId(clubId);
        dto.setClubName(null); // có thể fill từ Billardclub repo nếu cần
        dto.setRevenueByMonth(revenueByMonth);
        dto.setSalaryByMonth(salaryByMonth);
        dto.setTopProducts(topProducts);

        return dto;
    }

    /**
     * ✅ REVENUE: Tính doanh thu theo tháng dựa trên Bills đã Paid
     * Logic từ StaffBillingController - chỉ tính bills có status "Paid"
     */
    private List<MonthlyRevenueDTO> calculateMonthlyRevenue(
            Integer customerId,
            Integer clubId,
            LocalDate from,
            LocalDate to
    ) {
        // Lấy tất cả bills Paid trong khoảng thời gian
        List<Bill> paidBills = billRepo.findByClubID_IdAndCustomerID_IdAndBillStatusIgnoreCaseAndCreatedDateBetween(
                clubId,
                customerId,
                "Paid",
                from.atStartOfDay().toInstant(java.time.ZoneOffset.UTC),
                to.plusDays(1).atStartOfDay().toInstant(java.time.ZoneOffset.UTC)
        );

        // Group by month và tính tổng FinalAmount
        Map<String, BigDecimal> revenueMap = new LinkedHashMap<>();

        for (Bill bill : paidBills) {
            if (bill.getCreatedDate() == null) continue;

            String month = java.time.LocalDateTime
                    .ofInstant(bill.getCreatedDate(), java.time.ZoneId.systemDefault())
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));

            // Lấy FinalAmount, nếu null thì tính từ components
            BigDecimal amount = bill.getFinalAmount();
            if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
                BigDecimal tableCost = bill.getTotalTableCost() != null ? bill.getTotalTableCost() : BigDecimal.ZERO;
                BigDecimal productCost = bill.getTotalProductCost() != null ? bill.getTotalProductCost() : BigDecimal.ZERO;
                BigDecimal discount = bill.getDiscountAmount() != null ? bill.getDiscountAmount() : BigDecimal.ZERO;
                amount = tableCost.add(productCost).subtract(discount);
            }

            revenueMap.merge(month, amount, BigDecimal::add);
        }

        return revenueMap.entrySet().stream()
                .map(e -> new MonthlyRevenueDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * ✅ SALARY: Tính lương theo tháng với logic CHÍNH XÁC 100% từ StaffController.payrollSummary()
     * - FullTime: Salary cố định mỗi tháng có shifts (bất kể status)
     * - PartTime: totalShifts × 4h × hourlyRate + nightBonus (chỉ loại trừ Absent)
     */
    private List<MonthlySalaryDTO> calculateMonthlySalaryWithStaffLogic(
            Integer customerId,
            Integer clubId,
            LocalDate from,
            LocalDate to
    ) {
        // Lấy tất cả employees thuộc club/customer
        List<Employee> employees = employeeRepo.findByClubID_IdAndCustomerID_Id(clubId, customerId)
                .stream()
                .filter(e -> e.getIsActive() != null && e.getIsActive())
                .collect(Collectors.toList());

        Map<String, BigDecimal> salaryMap = new LinkedHashMap<>();

//        for (Employee emp : employees) {
//            String employeeType = emp.getEmployeeType();
//
//            // Lấy TẤT CẢ shifts trong khoảng thời gian (giống StaffController)
//            List<Employeeshift> allShifts = employeeshiftRepo
//                    .findByEmployeeID_IdAndShiftDateBetween(emp.getId(), from, to);
//
//            if ("PartTime".equalsIgnoreCase(employeeType)) {
//                // ✅ FullTime: Salary cố định mỗi tháng có shifts (BẤT KỂ STATUS)
//                BigDecimal monthlySalary = emp.getSalary() != null ? emp.getSalary() : BigDecimal.ZERO;
//
//                // Lấy các tháng distinct mà employee này có shift (không filter status)
//                Set<String> workedMonths = allShifts.stream()
//                        .map(s -> s.getShiftDate().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM")))
//                        .collect(Collectors.toSet());
//
//                // Cộng salary vào mỗi tháng đã làm việc
//                for (String month : workedMonths) {
//                    salaryMap.merge(month, monthlySalary, BigDecimal::add);
//                }
//
//            } else {
//                // ✅ PartTime: Logic CHÍNH XÁC từ StaffController.payrollSummary()
//
//                // Group shifts by month
//                Map<String, List<Employeeshift>> shiftsByMonth = allShifts.stream()
//                        .collect(Collectors.groupingBy(s ->
//                                s.getShiftDate().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"))
//                        ));
//
//                for (Map.Entry<String, List<Employeeshift>> entry : shiftsByMonth.entrySet()) {
//                    String month = entry.getKey();
//                    List<Employeeshift> monthShifts = entry.getValue();
//
//                    // ✅ totalShifts: CHỈ loại trừ Absent (giống StaffController line ~245)
//                    long totalShifts = monthShifts.stream()
//                            .filter(s -> s.getStatus() == null || !s.getStatus().equalsIgnoreCase("Absent"))
//                            .count();
//
//                    // ✅ nightShifts: CHỈ loại trừ Absent (giống StaffController line ~248)
//                    long nightShifts = monthShifts.stream()
//                            .filter(s -> s.getStatus() == null || !s.getStatus().equalsIgnoreCase("Absent"))
//                            .filter(this::isNightShift)
//                            .count();
//
//                    BigDecimal hourlyRate = emp.getHourlyRate() != null ? emp.getHourlyRate() : BigDecimal.ZERO;
//
//                    // ✅ nightBonus = nightShifts × 20,000 VND (StaffController line ~258)
//                    BigDecimal nightBonus = BigDecimal.valueOf(nightShifts * 20000L);
//
//                    // ✅ shiftPay = totalShifts × 4h × hourlyRate (StaffController line ~266-268)
//                    BigDecimal safeHourly = hourlyRate == null ? BigDecimal.ZERO : hourlyRate;
//                    BigDecimal shiftPay = BigDecimal.valueOf(totalShifts)
//                            .multiply(BigDecimal.valueOf(4))
//                            .multiply(safeHourly);
//
//                    // ✅ totalPay = shiftPay + nightBonus (StaffController line ~269)
//                    BigDecimal totalPay = shiftPay.add(nightBonus == null ? BigDecimal.ZERO : nightBonus);
//
//                    salaryMap.merge(month, totalPay, BigDecimal::add);
//                }
//            }
//        }
        for (Employee emp : employees) {
            String employeeType = emp.getEmployeeType() != null ? emp.getEmployeeType() : "";

            // Lấy TẤT CẢ shifts trong khoảng thời gian
            List<Employeeshift> allShifts = employeeshiftRepo
                    .findByEmployeeID_IdAndShiftDateBetween(emp.getId(), from, to);

            if ("FullTime".equalsIgnoreCase(employeeType)) {
                // FullTime: fixed monthly salary for each month that has any shifts (any status)
                BigDecimal monthlySalary = emp.getSalary() != null ? emp.getSalary() : BigDecimal.ZERO;

                Set<String> workedMonths = allShifts.stream()
                        .map(s -> s.getShiftDate().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM")))
                        .collect(Collectors.toSet());

                for (String month : workedMonths) {
                    salaryMap.merge(month, monthlySalary, BigDecimal::add);
                }

            } else {
                // PartTime: unchanged existing logic (hourlyRate × 4h × totalShifts + night bonus)
                Map<String, List<Employeeshift>> shiftsByMonth = allShifts.stream()
                        .collect(Collectors.groupingBy(s ->
                                s.getShiftDate().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"))
                        ));

                for (Map.Entry<String, List<Employeeshift>> entry : shiftsByMonth.entrySet()) {
                    String month = entry.getKey();
                    List<Employeeshift> monthShifts = entry.getValue();

                    long totalShifts = monthShifts.stream()
                            .filter(s -> s.getStatus() == null || !s.getStatus().equalsIgnoreCase("Absent"))
                            .count();

                    long nightShifts = monthShifts.stream()
                            .filter(s -> s.getStatus() == null || !s.getStatus().equalsIgnoreCase("Absent"))
                            .filter(this::isNightShift)
                            .count();

                    BigDecimal hourlyRate = emp.getHourlyRate() != null ? emp.getHourlyRate() : BigDecimal.ZERO;
                    BigDecimal nightBonus = BigDecimal.valueOf(nightShifts * 20000L);
                    BigDecimal shiftPay = BigDecimal.valueOf(totalShifts)
                            .multiply(BigDecimal.valueOf(4))
                            .multiply(hourlyRate);

                    BigDecimal totalPay = shiftPay.add(nightBonus);
                    salaryMap.merge(month, totalPay, BigDecimal::add);
                }
            }
        }

        return salaryMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new MonthlySalaryDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * ✅ TOP PRODUCTS: Tính top sản phẩm theo profit từ BillDetails
     * Logic từ StaffBillingController - chỉ tính bills đã Paid
     */
    private List<TopProductStatDTO> calculateTopProducts(
            Integer customerId,
            Integer clubId,
            LocalDate from,
            LocalDate to,
            int topN
    ) {
        // Lấy tất cả BillDetails từ các bills Paid trong khoảng thời gian
        List<Billdetail> details = billdetailRepo.findByClubID_IdAndCustomerID_Id(clubId, customerId)
                .stream()
                .filter(d -> {
                    Bill bill = d.getBillID();
                    if (bill == null || bill.getCreatedDate() == null) return false;
                    if (!"Paid".equalsIgnoreCase(bill.getBillStatus())) return false;

                    LocalDate billDate = java.time.LocalDateTime
                            .ofInstant(bill.getCreatedDate(), java.time.ZoneId.systemDefault())
                            .toLocalDate();

                    return !billDate.isBefore(from) && !billDate.isAfter(to);
                })
                .collect(Collectors.toList());

        // Group by product và tính profit
        Map<Integer, ProductProfit> productProfitMap = new HashMap<>();

        for (Billdetail detail : details) {
            Product product = detail.getProductID();
            if (product == null) continue;

            Integer productId = product.getId();
            Integer quantity = detail.getQuantity() != null ? detail.getQuantity() : 0;
            BigDecimal price = product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO;
            BigDecimal costPrice = product.getCostPrice() != null ? product.getCostPrice() : BigDecimal.ZERO;
            BigDecimal profitPerUnit = price.subtract(costPrice);
            BigDecimal totalProfit = profitPerUnit.multiply(new BigDecimal(quantity));

            productProfitMap.compute(productId, (id, existing) -> {
                if (existing == null) {
                    return new ProductProfit(
                            productId,
                            product.getProductName(),
                            product.getCategory(),
                            (long) quantity,
                            profitPerUnit,
                            totalProfit
                    );
                } else {
                    existing.qtySold += quantity;
                    existing.totalProfit = existing.totalProfit.add(totalProfit);
                    return existing;
                }
            });
        }

        // Sort by totalProfit descending và lấy top N
        return productProfitMap.values().stream()
                .sorted((p1, p2) -> p2.totalProfit.compareTo(p1.totalProfit))
                .limit(topN)
                .map(p -> {
                    TopProductStatDTO dto = new TopProductStatDTO();
                    dto.setProductId(p.productId);
                    dto.setProductName(p.productName);
                    dto.setCategory(p.category);
                    dto.setQtySold(p.qtySold);
                    dto.setProfitPerUnit(p.profitPerUnit);
                    dto.setTotalProfit(p.totalProfit);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * ✅ EMPLOYEE SALARY DETAILS: Chi tiết lương từng nhân viên
     * Logic CHÍNH XÁC 100% từ StaffController.payrollSummary()
     */
    public List<EmployeeSalaryDetailDTO> getEmployeeSalaryDetails(
            Integer customerId,
            Integer clubId,
            LocalDate from,
            LocalDate to
    ) {
        if (to == null) to = LocalDate.now();
        if (from == null) from = to.withDayOfMonth(1);

        List<Employee> employees = employeeRepo.findByClubID_IdAndCustomerID_Id(clubId, customerId)
                .stream()
                .filter(e -> e.getIsActive() != null && e.getIsActive())
                .collect(Collectors.toList());

        List<EmployeeSalaryDetailDTO> result = new ArrayList<>();

        for (Employee emp : employees) {
            // ✅ Lấy TẤT CẢ shifts (giống StaffController line ~239)
            List<Employeeshift> allShifts = employeeshiftRepo
                    .findByEmployeeID_IdAndShiftDateBetween(emp.getId(), from, to);

            String employeeType = emp.getEmployeeType();
            BigDecimal baseSalary;
            BigDecimal calculatedSalary;

            // ✅ totalHours: SUM tất cả HoursWorked (giống StaffController line ~253)
            double totalHours = allShifts.stream()
                    .mapToDouble(s -> s.getHoursWorked() != null ? s.getHoursWorked().doubleValue() : 0.0)
                    .sum();

            // ✅ scheduledShifts: Tổng số shifts (giống StaffController line ~241)
            int scheduledShifts = allShifts.size();

            // ✅ totalShifts: CHỈ loại trừ Absent (giống StaffController line ~242-245)
            long totalShifts = allShifts.stream()
                    .filter(s -> s.getStatus() == null || !s.getStatus().equalsIgnoreCase("Absent"))
                    .count();

            if ("FullTime".equalsIgnoreCase(employeeType)) {
                // ✅ FullTime: Salary × số tháng có shifts (BẤT KỂ STATUS)
                baseSalary = emp.getSalary() != null ? emp.getSalary() : BigDecimal.ZERO;

                long distinctMonths = allShifts.stream()
                        .map(s -> s.getShiftDate().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM")))
                        .distinct()
                        .count();

                calculatedSalary = baseSalary.multiply(new BigDecimal(distinctMonths));

            } else {
                // ✅ PartTime: totalShifts × 4h × hourlyRate + nightBonus
                baseSalary = emp.getHourlyRate() != null ? emp.getHourlyRate() : BigDecimal.ZERO;

                // ✅ nightShifts: CHỈ loại trừ Absent (giống StaffController line ~246-249)
                long nightShifts = allShifts.stream()
                        .filter(s -> s.getStatus() == null || !s.getStatus().equalsIgnoreCase("Absent"))
                        .filter(this::isNightShift)
                        .count();

                // ✅ nightBonus = nightShifts × 20,000 (StaffController line ~258)
                BigDecimal nightBonus = BigDecimal.valueOf(nightShifts * 20000L);

                // ✅ shiftPay = totalShifts × 4h × hourlyRate (StaffController line ~266-268)
                BigDecimal safeHourly = baseSalary == null ? BigDecimal.ZERO : baseSalary;
                BigDecimal shiftPay = BigDecimal.valueOf(totalShifts)
                        .multiply(BigDecimal.valueOf(4))
                        .multiply(safeHourly);

                // ✅ totalPay = shiftPay + nightBonus (StaffController line ~269)
                calculatedSalary = shiftPay.add(nightBonus == null ? BigDecimal.ZERO : nightBonus);
            }

            result.add(new EmployeeSalaryDetailDTO(
                    emp.getId(),
                    emp.getEmployeeName(),
                    employeeType,
                    baseSalary,
                    totalHours,
                    (int) totalShifts, // Dùng totalShifts thay vì scheduledShifts
                    calculatedSalary
            ));
        }

        // Sort by calculatedSalary descending
        result.sort((a, b) -> b.getCalculatedSalary().compareTo(a.getCalculatedSalary()));

        return result;
    }

    /**
     * ✅ Helper: Kiểm tra ca đêm (từ StaffController)
     * Ca đêm: >= 22:00 hoặc < 06:00
     */
    private boolean isNightShift(Employeeshift s) {
        if (s.getStartTime() == null) return false;
        int startMin = s.getStartTime().getHour() * 60 + s.getStartTime().getMinute();
        return startMin >= 22 * 60 || startMin < 6 * 60;
    }

    /**
     * Helper class để group product profit
     */
    private static class ProductProfit {
        Integer productId;
        String productName;
        String category;
        long qtySold;
        BigDecimal profitPerUnit;
        BigDecimal totalProfit;

        ProductProfit(Integer productId, String productName, String category,
                      long qtySold, BigDecimal profitPerUnit, BigDecimal totalProfit) {
            this.productId = productId;
            this.productName = productName;
            this.category = category;
            this.qtySold = qtySold;
            this.profitPerUnit = profitPerUnit;
            this.totalProfit = totalProfit;
        }
    }
}