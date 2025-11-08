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

/**
 * Controller cung cấp các endpoint export Excel cho Dashboard:
 *  - Export doanh thu (theo tháng)
 *  - Export tổng lương (theo tháng)
 *  - Export top products (theo profit)
 *
 * Lưu ý:
 *  - DashboardService.buildClubDashboard(...) được dùng để lấy dữ liệu.
 *  - ExcelExporter.exportTable(header, rows) trả về byte[] của file xlsx.
 *  - Bạn có thể chỉnh đường dẫn hoặc tên file theo ý muốn.
 */
@RestController
@RequestMapping("/api/customers/{customerId}/clubs/{clubId}/export")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Export doanh thu theo tháng cho 1 club dưới dạng Excel.
     * GET /api/customers/{customerId}/clubs/{clubId}/export/revenue
     */
    @GetMapping("/revenue")
    public ResponseEntity<byte[]> exportRevenue(
            @PathVariable Integer customerId,
            @PathVariable Integer clubId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        try {
            // Lấy dashboard data (mặc định from = đầu tháng, to = today nếu null)
            ClubDashboardDTO dto = dashboardService.buildClubDashboard(customerId, clubId, from, to, 5);

            // Build rows
            String[] header = new String[] {"Month (yyyy-MM)", "Revenue"};
            List<Object[]> rows = new ArrayList<>();
            for (MonthlyRevenueDTO r : dto.getRevenueByMonth()) {
                rows.add(new Object[] { r.getMonth(), r.getRevenue() });
            }

            byte[] bytes = ExcelExporter.exportTable(header, rows);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename("revenue_club_" + clubId + ".xlsx").build());
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception ex) {
            // Log exception nếu bạn có logger (omitted)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Export tổng lương nhân viên theo tháng cho 1 club.
     * GET /api/customers/{customerId}/clubs/{clubId}/export/salaries
     */
    @GetMapping("/salaries")
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
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Export top products by profit cho 1 club.
     * GET /api/customers/{customerId}/clubs/{clubId}/export/top-products?topN=5
     */
    @GetMapping("/top-products")
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
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}