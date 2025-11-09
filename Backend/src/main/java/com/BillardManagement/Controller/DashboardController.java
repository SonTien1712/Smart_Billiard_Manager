package com.BillardManagement.Controller;

import com.BillardManagement.DTO.Dashboard.*;
import com.BillardManagement.Service.DashboardService;
import com.BillardManagement.Util.ExcelExporter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // ==================== ✅ ENDPOINT MỚI - LẤY DASHBOARD DATA ====================

    /**
     * GET /api/customers/{customerId}/clubs/{clubId}/dashboard
     * Trả về ClubDashboardDTO cho frontend hiển thị charts & tables
     */
    @GetMapping("/customers/{customerId}/clubs/{clubId}/dashboard")
    public ResponseEntity<ClubDashboardDTO> getClubDashboard(
            @PathVariable Integer customerId,
            @PathVariable Integer clubId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "5") int topN
    ) {
        try {
            ClubDashboardDTO dto = dashboardService.buildClubDashboard(customerId, clubId, from, to, topN);
            return ResponseEntity.ok(dto);
        } catch (Exception ex) {
            // Log exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ==================== EXPORT ENDPOINTS (GIỮ NGUYÊN) ====================

    @GetMapping("/customers/{customerId}/clubs/{clubId}/export/revenue")
    public ResponseEntity<byte[]> exportRevenue(
            @PathVariable Integer customerId,
            @PathVariable Integer clubId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        try {
            ClubDashboardDTO dto = dashboardService.buildClubDashboard(customerId, clubId, from, to, 5);

            String[] header = new String[] {"Month (yyyy-MM)", "Revenue"};
            List<Object[]> rows = new ArrayList<>();
            for (MonthlyRevenueDTO r : dto.getRevenueByMonth()) {
                rows.add(new Object[] { r.getMonth(), r.getRevenue() });
            }

            byte[] bytes = ExcelExporter.exportTable(header, rows);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename("revenue_club_" + clubId + ".xlsx").build());
            headers.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/customers/{customerId}/clubs/{clubId}/export/salaries")
    public ResponseEntity<byte[]> exportSalaries(
            @PathVariable Integer customerId,
            @PathVariable Integer clubId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        try {
            ClubDashboardDTO dto = dashboardService.buildClubDashboard(customerId, clubId, from, to, 5);

            String[] header = new String[] {"Month (yyyy-MM)", "TotalSalary"};
            List<Object[]> rows = new ArrayList<>();
            for (MonthlySalaryDTO s : dto.getSalaryByMonth()) {
                rows.add(new Object[] { s.getMonth(), s.getTotalSalary() });
            }

            byte[] bytes = ExcelExporter.exportTable(header, rows);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename("salaries_club_" + clubId + ".xlsx").build());
            headers.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/customers/{customerId}/clubs/{clubId}/export/employee-salaries")
    public ResponseEntity<byte[]> exportEmployeeSalaries(
            @PathVariable Integer customerId,
            @PathVariable Integer clubId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        try {
            List<EmployeeSalaryDetailDTO> salaryDetails =
                    dashboardService.getEmployeeSalaryDetails(customerId, clubId, from, to);

            String[] header = new String[] {
                    "Employee ID",
                    "Employee Name",
                    "Type",
                    "Base Salary/Rate",
                    "Total Hours",
                    "Total Shifts",
                    "Calculated Salary"
            };

            List<Object[]> rows = new ArrayList<>();
            for (EmployeeSalaryDetailDTO emp : salaryDetails) {
                rows.add(new Object[] {
                        emp.getEmployeeId(),
                        emp.getEmployeeName(),
                        emp.getEmployeeType(),
                        emp.getBaseSalary(),
                        emp.getTotalHoursWorked(),
                        emp.getTotalShifts(),
                        emp.getCalculatedSalary()
                });
            }

            byte[] bytes = ExcelExporter.exportTable(header, rows);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename("employee_salaries_club_" + clubId + ".xlsx").build());
            headers.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/customers/{customerId}/clubs/{clubId}/export/top-products")
    public ResponseEntity<byte[]> exportTopProducts(
            @PathVariable Integer customerId,
            @PathVariable Integer clubId,
            @RequestParam(defaultValue = "5") int topN,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        try {
            ClubDashboardDTO dto = dashboardService.buildClubDashboard(customerId, clubId, from, to, topN);

            String[] header = new String[] {"Category", "ProductName", "QtySold", "ProfitPerUnit", "TotalProfit"};
            List<Object[]> rows = new ArrayList<>();
            for (TopProductStatDTO p : dto.getTopProducts()) {
                rows.add(new Object[] {
                        p.getCategory(),
                        p.getProductName(),
                        p.getQtySold(),
                        p.getProfitPerUnit(),
                        p.getTotalProfit()
                });
            }

            byte[] bytes = ExcelExporter.exportTable(header, rows);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename("top_products_club_" + clubId + ".xlsx").build());
            headers.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}