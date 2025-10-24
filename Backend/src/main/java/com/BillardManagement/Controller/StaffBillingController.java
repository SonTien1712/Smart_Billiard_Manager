package com.BillardManagement.Controller;

import com.BillardManagement.DTO.Response.BillSummaryDTO;
import com.BillardManagement.DTO.Response.BillDetailDTO;
import com.BillardManagement.DTO.Response.BillItemDTO;
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
import java.util.Map;
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
    private final com.BillardManagement.Repository.BilldetailRepo billdetailRepo;

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
                    active.map(Bill::getStartTime).orElse(null),
                    active.map(Bill::getId).orElse(null),
                    active.map(b -> b.getEmployeeID() != null ? b.getEmployeeID().getId() : null).orElse(null)
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
    public ResponseEntity<List<ProductDTO>> listProducts(
            @RequestParam(value = "clubId", required = false) Integer clubId,
            @RequestParam(value = "customerId", required = false) Integer customerId
    ) {
        var entities = (clubId != null)
                ? productRepo.findByClubID_IdAndIsActiveTrueOrderByProductNameAsc(clubId)
                : (customerId != null)
                    ? productRepo.findByCustomerID_IdAndIsActiveTrueOrderByProductNameAsc(customerId)
                    : productRepo.findByIsActiveTrueOrderByProductNameAsc();
        var dtos = entities.stream()
                .map(p -> new ProductDTO(p.getId(), p.getProductName(), p.getPrice(), p.getCategory()))
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/bills")
    public ResponseEntity<List<BillSummaryDTO>> listRecentBills(
            @RequestParam(value = "clubId", required = false) Integer clubId,
            @RequestParam(value = "customerId", required = false) Integer customerId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "employeeId", required = false) Integer employeeId,
            @RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit
    ) {
        List<Bill> bills;
        boolean wantPaid = status != null && status.equalsIgnoreCase("Paid");
        if (clubId != null && status != null) {
            bills = wantPaid
                    ? billRepo.findTop10ByClubID_IdAndBillStatusIgnoreCaseOrderByEndTimeDesc(clubId, status)
                    : billRepo.findTop10ByClubID_IdAndBillStatusIgnoreCaseOrderByCreatedDateDesc(clubId, status);
        } else if (customerId != null && status != null) {
            bills = wantPaid
                    ? billRepo.findTop10ByCustomerID_IdAndBillStatusIgnoreCaseOrderByEndTimeDesc(customerId, status)
                    : billRepo.findTop10ByCustomerID_IdAndBillStatusIgnoreCaseOrderByCreatedDateDesc(customerId, status);
        } else if (status != null) {
            bills = wantPaid
                    ? billRepo.findTop10ByBillStatusIgnoreCaseOrderByEndTimeDesc(status)
                    : billRepo.findTop10ByBillStatusIgnoreCaseOrderByCreatedDateDesc(status);
        } else {
            bills = billRepo.findTop10ByOrderByCreatedDateDesc();
        }

        // Optional filter by employee (only show bills created/handled by this employee)
        if (employeeId != null) {
            bills = bills.stream()
                    .filter(b -> b.getEmployeeID() != null && employeeId.equals(b.getEmployeeID().getId()))
                    .collect(Collectors.toList());
        }

        // Trim to requested limit after filtering
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
            java.time.Instant createdOrCompleted = b.getEndTime() != null ? b.getEndTime() : b.getCreatedDate();
            return new BillSummaryDTO(b.getId(), tableName, amount, b.getBillStatus(), createdOrCompleted);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // Get a full bill with items
    @GetMapping("/bills/{billId}")
    public ResponseEntity<BillDetailDTO> getBillById(@PathVariable("billId") Integer billId) {
        var opt = billRepo.findViewById(billId);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Bill b = opt.get();

        var items = billdetailRepo.findWithProductByBill(billId).stream().map(d -> new BillItemDTO(
                d.getId(),
                d.getProductID() != null ? d.getProductID().getId() : null,
                d.getProductID() != null ? d.getProductID().getProductName() : null,
                d.getQuantity(),
                d.getUnitPrice(),
                d.getSubTotal()
        )).collect(java.util.stream.Collectors.toList());

        BillDetailDTO dto = new BillDetailDTO(
                b.getId(),
                b.getTableID() != null ? b.getTableID().getTableName() : "",
                b.getBillStatus(),
                b.getStartTime(),
                b.getEndTime(),
                b.getTotalHours(),
                b.getTotalTableCost(),
                b.getTotalProductCost(),
                b.getDiscountAmount(),
                b.getFinalAmount(),
                items
        );
        return ResponseEntity.ok(dto);
    }

    // --- Bill items draft management ---
    @PostMapping("/bills/{billId}/items")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<BillItemDTO> addBillItem(
            @PathVariable("billId") Integer billId,
            @org.springframework.web.bind.annotation.RequestBody java.util.Map<String, Object> payload,
            @RequestParam(value = "employeeId", required = false) Integer employeeId
    ) {
        var opt = billRepo.findById(billId);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Bill b = opt.get();
        if (b.getEndTime() != null) return ResponseEntity.status(409).build();
        if (b.getEmployeeID() != null && employeeId != null && !b.getEmployeeID().getId().equals(employeeId)) {
            return ResponseEntity.status(403).build();
        }

        Integer productId = null;
        Integer qty = 1;
        if (payload != null) {
            Object pid = payload.get("productId");
            Object q = payload.get("quantity");
            if (pid != null) try { productId = Integer.valueOf(pid.toString()); } catch (Exception ignored) {}
            if (q != null) try { qty = Integer.valueOf(q.toString()); } catch (Exception ignored) {}
        }
        if (productId == null || qty == null) return ResponseEntity.badRequest().build();
        if (qty == 0) return ResponseEntity.status(400).build();

        var pOpt = productRepo.findById(productId);
        if (pOpt.isEmpty()) return ResponseEntity.badRequest().build();
        var p = pOpt.get();

        var existing = billdetailRepo.findByBillID_IdAndProductID_Id(billId, productId);
        com.BillardManagement.Entity.Billdetail d;
        if (existing.isPresent()) {
            d = existing.get();
            int newQty = (d.getQuantity() == null ? 0 : d.getQuantity()) + qty;
            if (newQty <= 0) {
                billdetailRepo.delete(d);
                // update product total snapshot
                b.setTotalProductCost(recalcProductTotal(billId));
                billRepo.save(b);
                return ResponseEntity.ok(new BillItemDTO(null, productId, p.getProductName(), 0, p.getPrice(), java.math.BigDecimal.ZERO));
            }
            d.setQuantity(newQty);
        } else {
            d = new com.BillardManagement.Entity.Billdetail();
            d.setBillID(b);
            d.setClubID(b.getClubID());
            d.setCustomerID(b.getCustomerID());
            d.setProductID(p);
            d.setQuantity(Math.max(1, qty));
        }
        d.setUnitPrice(p.getPrice());
        d.setSubTotal(p.getPrice().multiply(new java.math.BigDecimal(d.getQuantity())));
        d = billdetailRepo.save(d);
        b.setTotalProductCost(recalcProductTotal(billId));
        billRepo.save(b);

        return ResponseEntity.ok(new BillItemDTO(
                d.getId(),
                productId,
                p.getProductName(),
                d.getQuantity(),
                d.getUnitPrice(),
                d.getSubTotal()
        ));
    }

    @PutMapping("/bills/{billId}/items/{itemId}")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<BillItemDTO> updateBillItem(
            @PathVariable("billId") Integer billId,
            @PathVariable("itemId") Integer itemId,
            @org.springframework.web.bind.annotation.RequestBody java.util.Map<String, Object> payload,
            @RequestParam(value = "employeeId", required = false) Integer employeeId
    ) {
        var opt = billRepo.findById(billId);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Bill b = opt.get();
        if (b.getEndTime() != null) return ResponseEntity.status(409).build();
        if (b.getEmployeeID() != null && employeeId != null && !b.getEmployeeID().getId().equals(employeeId)) {
            return ResponseEntity.status(403).build();
        }
        var dOpt = billdetailRepo.findById(itemId);
        if (dOpt.isEmpty()) return ResponseEntity.notFound().build();
        var d = dOpt.get();
        if (!d.getBillID().getId().equals(billId)) return ResponseEntity.status(400).build();

        Integer qty = null;
        if (payload != null) {
            Object q = payload.get("quantity");
            if (q != null) try { qty = Integer.valueOf(q.toString()); } catch (Exception ignored) {}
        }
        if (qty == null) return ResponseEntity.badRequest().build();
        if (qty <= 0) {
            billdetailRepo.delete(d);
            b.setTotalProductCost(recalcProductTotal(billId));
            billRepo.save(b);
            return ResponseEntity.ok(new BillItemDTO(itemId, d.getProductID() != null ? d.getProductID().getId() : null, d.getProductID() != null ? d.getProductID().getProductName() : null, 0, d.getUnitPrice(), java.math.BigDecimal.ZERO));
        }
        d.setQuantity(qty);
        java.math.BigDecimal price = d.getUnitPrice() != null ? d.getUnitPrice() : (d.getProductID() != null ? d.getProductID().getPrice() : java.math.BigDecimal.ZERO);
        d.setUnitPrice(price);
        d.setSubTotal(price.multiply(new java.math.BigDecimal(qty)));
        d = billdetailRepo.save(d);
        b.setTotalProductCost(recalcProductTotal(billId));
        billRepo.save(b);

        return ResponseEntity.ok(new BillItemDTO(
                d.getId(),
                d.getProductID() != null ? d.getProductID().getId() : null,
                d.getProductID() != null ? d.getProductID().getProductName() : null,
                d.getQuantity(),
                d.getUnitPrice(),
                d.getSubTotal()
        ));
    }

    @DeleteMapping("/bills/{billId}/items/{itemId}")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<Void> deleteBillItem(
            @PathVariable("billId") Integer billId,
            @PathVariable("itemId") Integer itemId,
            @RequestParam(value = "employeeId", required = false) Integer employeeId
    ) {
        var opt = billRepo.findById(billId);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Bill b = opt.get();
        if (b.getEndTime() != null) return ResponseEntity.status(409).build();
        if (b.getEmployeeID() != null && employeeId != null && !b.getEmployeeID().getId().equals(employeeId)) {
            return ResponseEntity.status(403).build();
        }
        var dOpt = billdetailRepo.findById(itemId);
        if (dOpt.isEmpty()) return ResponseEntity.notFound().build();
        var d = dOpt.get();
        if (!d.getBillID().getId().equals(billId)) return ResponseEntity.status(400).build();
        billdetailRepo.delete(d);
        b.setTotalProductCost(recalcProductTotal(billId));
        billRepo.save(b);
        return ResponseEntity.ok().build();
    }

    private java.math.BigDecimal recalcProductTotal(Integer billId) {
        return billdetailRepo.findByBillID_Id(billId).stream()
                .map(x -> x.getSubTotal() == null ? java.math.BigDecimal.ZERO : x.getSubTotal())
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    // Complete/checkout bill: close active session and compute costs
    @PatchMapping("/bills/{billId}/complete")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<BillSummaryDTO> completeBill(
            @PathVariable("billId") Integer billId,
            @RequestParam(value = "employeeId", required = false) Integer employeeId,
            @org.springframework.web.bind.annotation.RequestBody(required = false) java.util.Map<String, Object> payload
    ) {
        var opt = billRepo.findById(billId); // locked by @Lock on repo
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Bill b = opt.get();

        // Only the owner (employee who opened) can complete
        if (b.getEmployeeID() != null && employeeId != null && !b.getEmployeeID().getId().equals(employeeId)) {
            return ResponseEntity.status(403).build();
        }

        // if already ended, just return summary
        if (b.getEndTime() == null) {
            Instant end = Instant.now();
            b.setEndTime(end);

            // Round playing time to nearest 6 minutes (0.1h increments):
            // if remainder >= 3m round up, else down
            java.math.BigDecimal hours = java.math.BigDecimal.ZERO;
            if (b.getStartTime() != null) {
                long minutes = java.time.Duration.between(b.getStartTime(), end).toMinutes();
                if (minutes < 0) minutes = 0;
                long rem = minutes % 6;
                long roundedMinutes = rem >= 3 ? minutes + (6 - rem) : minutes - rem;
                hours = new java.math.BigDecimal(roundedMinutes)
                        .divide(new java.math.BigDecimal(60), 2, java.math.RoundingMode.HALF_UP);
            }
            b.setTotalHours(hours);

            java.math.BigDecimal rate = java.math.BigDecimal.ZERO;
            if (b.getTableID() != null && b.getTableID().getHourlyRate() != null) {
                rate = b.getTableID().getHourlyRate();
            }
            java.math.BigDecimal tableCost = hours.multiply(rate);
            b.setTotalTableCost(tableCost);

            // Optionally accept product total and simple tax from client during checkout
            java.math.BigDecimal product = b.getTotalProductCost() == null ? java.math.BigDecimal.ZERO : b.getTotalProductCost();
            java.math.BigDecimal discount = b.getDiscountAmount() == null ? java.math.BigDecimal.ZERO : b.getDiscountAmount();
            if (payload != null) {
                Object pt = payload.get("productTotal");
                if (pt instanceof Number) {
                    product = new java.math.BigDecimal(pt.toString());
                } else if (pt instanceof String && !((String) pt).isBlank()) {
                    try { product = new java.math.BigDecimal((String) pt); } catch (Exception ignored) {}
                }

                Object disc = payload.get("discount");
                if (disc instanceof Number) {
                    discount = new java.math.BigDecimal(disc.toString());
                } else if (disc instanceof String && !((String) disc).isBlank()) {
                    try { discount = new java.math.BigDecimal((String) disc); } catch (Exception ignored) {}
                }
            }

            // If checkout includes items list, replace bill details accordingly and recalc product total
            if (payload != null && payload.get("items") instanceof List<?> list) {
                // Remove existing details to replace
                billdetailRepo.deleteByBillID_Id(b.getId());

                java.math.BigDecimal productSum = java.math.BigDecimal.ZERO;
                for (Object o : list) {
                    if (!(o instanceof Map<?, ?> m)) continue;
                    Object pid = m.get("productId");
                    Object qty = m.get("quantity");
                    if (pid == null || qty == null) continue;
                    Integer productId = null;
                    try { productId = Integer.valueOf(pid.toString()); } catch (Exception ignored) {}
                    Integer quantity = null;
                    try { quantity = Integer.valueOf(qty.toString()); } catch (Exception ignored) {}
                    if (productId == null || quantity == null || quantity <= 0) continue;
                    var pOpt = productRepo.findById(productId);
                    if (pOpt.isEmpty()) continue;
                    var p = pOpt.get();

                    var detail = new com.BillardManagement.Entity.Billdetail();
                    detail.setBillID(b);
                    detail.setClubID(b.getClubID());
                    detail.setCustomerID(b.getCustomerID());
                    detail.setProductID(p);
                    detail.setQuantity(quantity);
                    detail.setUnitPrice(p.getPrice());
                    java.math.BigDecimal sub = p.getPrice().multiply(new java.math.BigDecimal(quantity));
                    detail.setSubTotal(sub);
                    billdetailRepo.save(detail);
                    productSum = productSum.add(sub);
                }
                product = productSum; // prefer calculated sum from items
            }

            // Compute subtotal then optional tax
            java.math.BigDecimal subtotal = tableCost.add(product).subtract(discount);
            if (subtotal.signum() < 0) subtotal = java.math.BigDecimal.ZERO;

            java.math.BigDecimal taxAmount = java.math.BigDecimal.ZERO;
            if (payload != null) {
                Object tax = payload.get("taxPercent");
                java.math.BigDecimal taxPercent = java.math.BigDecimal.ZERO;
                if (tax instanceof Number) {
                    taxPercent = new java.math.BigDecimal(tax.toString());
                } else if (tax instanceof String && !((String) tax).isBlank()) {
                    try { taxPercent = new java.math.BigDecimal((String) tax); } catch (Exception ignored) {}
                }
                if (taxPercent.signum() > 0) {
                    taxAmount = subtotal.multiply(taxPercent).divide(new java.math.BigDecimal(100), 2, java.math.RoundingMode.HALF_UP);
                }
            }

            java.math.BigDecimal finalAmount = subtotal.add(taxAmount);
            if (finalAmount.signum() < 0) finalAmount = java.math.BigDecimal.ZERO;
            b.setFinalAmount(finalAmount);
            // Persist the product total if it came from client-side checkout
            b.setTotalProductCost(product);
            b.setBillStatus("Paid");

            b = billRepo.save(b);
        }

        String tableName = b.getTableID() != null ? b.getTableID().getTableName() : "";
        java.math.BigDecimal amount = b.getFinalAmount() != null ? b.getFinalAmount() : java.math.BigDecimal.ZERO;
        java.time.Instant when = b.getEndTime() != null ? b.getEndTime() : b.getCreatedDate();
        return ResponseEntity.ok(new BillSummaryDTO(b.getId(), tableName, amount, b.getBillStatus(), when));
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

        // Count and revenue by completion time (EndTime) for better accuracy
        long count = billRepo.countByEndTimeBetween(start, end);
        var paid = billRepo.findByBillStatusIgnoreCaseAndEndTimeBetween("Paid", start, end);
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

    // Cancel an active bill (e.g., opened wrong table).
    @PatchMapping("/bills/{billId}/cancel")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<Void> cancelBill(
            @PathVariable("billId") Integer billId,
            @RequestParam(value = "employeeId", required = false) Integer employeeId
    ) {
        var opt = billRepo.findById(billId); // locked
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Bill b = opt.get();

        // Only the owner (employee who opened) can cancel
        if (b.getEmployeeID() != null && employeeId != null && !b.getEmployeeID().getId().equals(employeeId)) {
            return ResponseEntity.status(403).build();
        }

        // If already paid, do not allow cancel
        if (b.getBillStatus() != null && b.getBillStatus().equalsIgnoreCase("Paid")) {
            return ResponseEntity.status(409).build();
        }

        b.setBillStatus("Cancelled");
        if (b.getEndTime() == null) b.setEndTime(Instant.now());
        b.setFinalAmount(java.math.BigDecimal.ZERO);
        if (b.getTotalTableCost() == null || b.getTotalTableCost().signum() != 0) b.setTotalTableCost(java.math.BigDecimal.ZERO);
        if (b.getTotalProductCost() == null || b.getTotalProductCost().signum() != 0) b.setTotalProductCost(java.math.BigDecimal.ZERO);
        if (b.getDiscountAmount() == null || b.getDiscountAmount().signum() != 0) b.setDiscountAmount(java.math.BigDecimal.ZERO);
        if (b.getTotalHours() == null || b.getTotalHours().signum() != 0) b.setTotalHours(java.math.BigDecimal.ZERO);
        billRepo.save(b);
        return ResponseEntity.ok().build();
    }
}
