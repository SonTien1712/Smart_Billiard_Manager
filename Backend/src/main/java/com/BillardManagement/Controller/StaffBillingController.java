package com.BillardManagement.Controller;

import com.BillardManagement.DTO.Response.BillSummaryDTO;
import com.BillardManagement.DTO.Response.TableDTO;
import com.BillardManagement.DTO.Response.DashboardStatsDTO;
import com.BillardManagement.DTO.Response.ProductDTO;
import com.BillardManagement.Entity.Bill;
import com.BillardManagement.Entity.Billiardtable;
import com.BillardManagement.Repository.BillRepo;
import com.BillardManagement.Repository.BilliardtableRepo;
import com.BillardManagement.Repository.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class StaffBillingController {

    private final BilliardtableRepo tableRepo;
    private final BillRepo billRepo;
    private final ProductRepo productRepo;

    @GetMapping("/tables")
    public ResponseEntity<List<TableDTO>> listTables(
            @RequestParam(value = "clubId", required = false) Integer clubId,
            @RequestParam(value = "customerId", required = false) Integer customerId,
            @RequestParam(value = "status", required = false) String status
    ) {
        List<Billiardtable> tables;
        if (clubId != null) tables = tableRepo.findByClubID_Id(clubId);
        else if (customerId != null) tables = tableRepo.findByCustomerID_Id(customerId);
        else tables = tableRepo.findAll();

        List<TableDTO> dtos = tables.stream().map(t -> {
            // Occupancy: table has an active bill (EndTime NULL)
            Optional<Bill> active = billRepo.findFirstByTableID_IdAndEndTimeIsNullOrderByStartTimeDesc(t.getId());
            String baseStatus = t.getTableStatus() == null ? "Available" : t.getTableStatus();
            String normalizedStatus = baseStatus.equalsIgnoreCase("Available")
                    ? (active.isPresent() ? "occupied" : "available")
                    : baseStatus.toLowerCase();

            // Map VN table type to UI terms where possible
            String type = t.getTableType();
            if (type != null) {
                String lower = type.toLowerCase();
                if (lower.contains("lỗ")) type = "Pool";
                else if (lower.contains("phăng")) type = "Snooker";
                else if (lower.contains("carom")) type = "Carom";
            }

            return new TableDTO(
                    t.getId(),
                    t.getTableName(),
                    type,
                    t.getHourlyRate(),
                    normalizedStatus,
                    active.map(Bill::getStartTime).orElse(null)
            );
        }).collect(Collectors.toList());

        if (status != null && !status.isBlank()) {
            String s = status.toLowerCase();
            dtos = dtos.stream().filter(x -> x.getStatus() != null && x.getStatus().equalsIgnoreCase(s)).collect(Collectors.toList());
        }

        // Order by name for consistent UI
        dtos.sort(Comparator.comparing(TableDTO::getName, String.CASE_INSENSITIVE_ORDER));
        return ResponseEntity.ok(dtos);
    }

    // Open a table: create an active bill if none exists (race-safe)
    @PostMapping("/tables/{tableId}/open")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<BillSummaryDTO> openTable(
            @PathVariable Integer tableId,
            @RequestParam("clubId") Integer clubId,
            @RequestParam("customerId") Integer customerId,
            @RequestParam(value = "employeeId", required = false) Integer employeeId
    ) {
        // Pessimistic lock on possible active bill row to avoid double-open
        // Serialize open attempts by locking the table row
        tableRepo.findLockedById(tableId).orElseThrow();
        var existing = billRepo.lockActiveBillByTable(tableId);
        if (existing.isPresent()) {
            return ResponseEntity.status(409).build();
        }

        // Build minimal Bill entity
        Bill b = new Bill();
        b.setStartTime(Instant.now());
        b.setBillStatus("Unpaid");
        // Set relations by id reference proxies
        var club = new com.BillardManagement.Entity.Billardclub();
        club.setId(clubId);
        b.setClubID(club);

        var customer = new com.BillardManagement.Entity.Customer();
        customer.setId(customerId);
        b.setCustomerID(customer);

        var table = new com.BillardManagement.Entity.Billiardtable();
        table.setId(tableId);
        b.setTableID(table);

        if (employeeId != null) {
            var emp = new com.BillardManagement.Entity.Employee();
            emp.setId(employeeId);
            b.setEmployeeID(emp);
        }

        b = billRepo.save(b);
        String tableName = (b.getTableID() != null && b.getTableID().getTableName() != null) ? b.getTableID().getTableName() : "";
        return ResponseEntity.ok(new BillSummaryDTO(b.getId(), tableName, BigDecimal.ZERO, b.getBillStatus(), b.getCreatedDate()));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductDTO>> listProducts(@RequestParam(value = "clubId", required = false) Integer clubId) {
        var entities = (clubId != null)
                ? productRepo.findByClubID_IdAndIsActiveTrueOrderByProductNameAsc(clubId)
                : productRepo.findByIsActiveTrueOrderByProductNameAsc();
        var dtos = entities.stream()
                .map(p -> new ProductDTO(p.getId(), p.getProductName(), p.getPrice(), p.getCategory()))
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/bills")
    public ResponseEntity<List<BillSummaryDTO>> listRecentBills(
            @RequestParam(value = "clubId", required = false) Integer clubId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit
    ) {
        List<Bill> bills;
        if (clubId != null && status != null) {
            bills = billRepo.findTop10ByClubID_IdAndBillStatusIgnoreCaseOrderByCreatedDateDesc(clubId, status);
        } else if (status != null) {
            bills = billRepo.findTop10ByBillStatusIgnoreCaseOrderByCreatedDateDesc(status);
        } else {
            bills = billRepo.findTop10ByOrderByCreatedDateDesc();
        }

        // Trim to requested limit
        if (bills.size() > limit) bills = bills.subList(0, limit);

        List<BillSummaryDTO> dtos = bills.stream().map(b -> {
            BigDecimal amount = b.getFinalAmount();
            if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
                BigDecimal tableCost = b.getTotalTableCost() == null ? BigDecimal.ZERO : b.getTotalTableCost();
                BigDecimal productCost = b.getTotalProductCost() == null ? BigDecimal.ZERO : b.getTotalProductCost();
                BigDecimal discount = b.getDiscountAmount() == null ? BigDecimal.ZERO : b.getDiscountAmount();
                amount = tableCost.add(productCost).subtract(discount);
            }

            String tableName = b.getTableID() != null ? b.getTableID().getTableName() : "";
            return new BillSummaryDTO(b.getId(), tableName, amount, b.getBillStatus(), b.getCreatedDate());
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/stats/today")
    public ResponseEntity<DashboardStatsDTO> todayStats(
            @RequestParam(value = "clubId", required = false) Integer clubId
    ) {
        // Start and end of today in system zone
        Instant start = java.time.LocalDate.now()
                .atStartOfDay(java.time.ZoneId.systemDefault())
                .toInstant();
        Instant end = java.time.LocalDate.now()
                .plusDays(1)
                .atStartOfDay(java.time.ZoneId.systemDefault())
                .toInstant();

        long count = billRepo.countByCreatedDateBetween(start, end);
        var paid = billRepo.findByBillStatusIgnoreCaseAndCreatedDateBetween("Paid", start, end);
        java.math.BigDecimal revenue = paid.stream()
                .map(b -> {
                    if (b.getFinalAmount() != null && b.getFinalAmount().signum() > 0) {
                        return b.getFinalAmount();
                    }
                    java.math.BigDecimal table = b.getTotalTableCost() == null ? java.math.BigDecimal.ZERO : b.getTotalTableCost();
                    java.math.BigDecimal product = b.getTotalProductCost() == null ? java.math.BigDecimal.ZERO : b.getTotalProductCost();
                    java.math.BigDecimal discount = b.getDiscountAmount() == null ? java.math.BigDecimal.ZERO : b.getDiscountAmount();
                    return table.add(product).subtract(discount);
                })
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        DashboardStatsDTO dto = new DashboardStatsDTO((int) count, revenue);
        return ResponseEntity.ok(dto);
    }

}
