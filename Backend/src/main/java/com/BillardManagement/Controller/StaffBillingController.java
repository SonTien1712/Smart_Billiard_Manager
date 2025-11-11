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
import com.BillardManagement.Repository.BilliardTableRepo;
import com.BillardManagement.Repository.ProductRepo;
import com.BillardManagement.Repository.PromotionRepository;
import com.BillardManagement.Entity.Promotion;
import com.BillardManagement.Entity.PromotionType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
/**
 * Controller xử lý nghiệp vụ thu ngân (Staff Billing).
 * - Quản lý bàn (mở bàn), bill đang chạy, item, khuyến mãi và checkout.
 * - Trạng thái bill: Unpaid (đang chạy), Pending (khóa chờ thanh toán), Paid (đã thanh toán), Cancelled (đã hủy).
 * - Quy tắc làm tròn giờ: mỗi 6 phút (0.1 giờ). Dư ≥ 3 phút làm tròn lên, ngược lại làm tròn xuống.
 * - Concurrency: sử dụng khóa bi quan ở một số truy vấn để tránh mở bàn trùng/ghi đè khi nhiều yêu cầu đồng thời.
 */
public class StaffBillingController {

    private final BilliardTableRepo tableRepo;
    private final BillRepo billRepo;
    private final ProductRepo productRepo;
    private final com.BillardManagement.Repository.BilldetailRepo billdetailRepo;
    private final PromotionRepository promotionRepository;
    private final com.BillardManagement.Service.InvoicePdfService invoicePdfService;

    /**
     * Tính lại giảm giá xem trước cho bill nếu đang có promotion.
     * - Tổng sản phẩm: ưu tiên tính lại từ Billdetail (tránh số liệu cũ)
     * - Giờ bàn: nếu bill ở trạng thái Pending dùng TotalHours đã khóa, ngược lại tính từ StartTime → hiện tại
     * - Lưu DiscountAmount và FinalAmount (trước thuế)
     * Tham số:
     * - b: Bill đang xử lý (nếu chưa có PromotionID thì hàm thoát ngay không tính giảm).
     * Ghi chú:
     * - Làm tròn thời gian theo bước 6 phút (0.1 giờ) giống logic khi checkout.
     * - Không thay đổi StartTime/EndTime; chỉ cập nhật các trường preview (DiscountAmount, FinalAmount).
     */
    private void recomputePromotionPreviewIfAny(Bill b) {
        if (b == null || b.getPromotionID() == null) return;
        Promotion p = b.getPromotionID();
        java.math.BigDecimal productTotal = recalcProductTotal(b.getId());
        if (productTotal == null) productTotal = b.getTotalProductCost() == null ? java.math.BigDecimal.ZERO : b.getTotalProductCost();
        java.math.BigDecimal tableCost = java.math.BigDecimal.ZERO;
        if (b.getTableID() != null && b.getTableID().getHourlyRate() != null) {
            // Nếu bill ở trạng thái Pending thì dùng giờ đã khóa; ngược lại tính từ StartTime đến hiện tại
            java.math.BigDecimal hours;
            if (b.getBillStatus() != null && b.getBillStatus().equalsIgnoreCase("Pending") && b.getTotalHours() != null) {
                hours = b.getTotalHours();
            } else if (b.getStartTime() != null) {
                long minutes = java.time.Duration.between(b.getStartTime(), java.time.Instant.now()).toMinutes();
                if (minutes < 0) minutes = 0;
                long rem = minutes % 6;
                long roundedMinutes = rem >= 3 ? minutes + (6 - rem) : minutes - rem;
                hours = new java.math.BigDecimal(roundedMinutes)
                        .divide(new java.math.BigDecimal(60), 2, java.math.RoundingMode.HALF_UP);
            } else {
                hours = java.math.BigDecimal.ZERO;
            }
            tableCost = hours.multiply(b.getTableID().getHourlyRate());
        }
        java.math.BigDecimal base = tableCost.add(productTotal);
        java.math.BigDecimal discount = java.math.BigDecimal.ZERO;
        if (p.getPromotionType() == PromotionType.PERCENTAGE) {
            discount = base.multiply(p.getPromotionValue())
                    .divide(new java.math.BigDecimal(100), 2, java.math.RoundingMode.HALF_UP);
        } else {
            discount = p.getPromotionValue() == null ? java.math.BigDecimal.ZERO : p.getPromotionValue();
            if (discount.compareTo(base) > 0) discount = base;
        }
        b.setDiscountAmount(discount);
        java.math.BigDecimal finalPreview = base.subtract(discount);
        if (finalPreview.signum() < 0) finalPreview = java.math.BigDecimal.ZERO;
        b.setFinalAmount(finalPreview);
    }

    

    // Khóa bill (đóng băng tổng) mà không thanh toán
    /**
     * Khóa bill (đưa về trạng thái Pending):
     * - Chốt giờ (TotalHours) theo thời điểm hiện tại và làm tròn 6 phút, tính và khóa TotalTableCost.
     * - Tính lại và khóa TotalProductCost từ Billdetail.
     * - Tính giảm giá xem trước (nếu đã áp promotion) và cập nhật FinalAmount bản xem trước.
     * - Không thanh toán, không trừ lượt promotion.
     * Tham số:
     * - billId (path): mã bill đang chạy cần khóa.
     * - payload (body, tùy chọn): { taxPercent?: number } để xem trước tổng sau thuế.
     * Trả về: BillDetailDTO chứa các tổng đã khóa để UI hiển thị.
     * Mã lỗi thường gặp:
     * - 404 nếu không tìm thấy bill.
     * - 409 nếu bill đã có EndTime (đã kết thúc) nên không thể khóa.
     */
    @PatchMapping("/bills/{billId}/finalize")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<BillDetailDTO> finalizeBill(
            @PathVariable("billId") Integer billId,
            @RequestBody(required = false) java.util.Map<String, Object> payload
    ) {
        var opt = billRepo.findById(billId); // đã khóa
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Bill b = opt.get();
        if (b.getEndTime() != null) return ResponseEntity.status(409).build(); // đã kết thúc

        // Tính số giờ theo thời điểm hiện tại và lưu vào TotalHours (khóa)
        java.math.BigDecimal hours = java.math.BigDecimal.ZERO;
        if (b.getStartTime() != null) {
            long minutes = java.time.Duration.between(b.getStartTime(), java.time.Instant.now()).toMinutes();
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

        // Recalc product total from details, lock it
        java.math.BigDecimal product = recalcProductTotal(b.getId());
        if (product == null) product = java.math.BigDecimal.ZERO;
        b.setTotalProductCost(product);

        // Apply promotion preview (uses locked hours if status Pending)
        b.setBillStatus("Pending");
        recomputePromotionPreviewIfAny(b);

        // Optional tax preview
        java.math.BigDecimal taxAmount = java.math.BigDecimal.ZERO;
        if (payload != null) {
            Object tax = payload.get("taxPercent");
            java.math.BigDecimal taxPercent = java.math.BigDecimal.ZERO;
            if (tax instanceof Number) {
                taxPercent = new java.math.BigDecimal(((Number) tax).toString());
            } else if (tax instanceof String && !((String) tax).isBlank()) {
                try { taxPercent = new java.math.BigDecimal((String) tax); } catch (Exception ignored) {}
            }
            if (taxPercent.signum() > 0) {
                java.math.BigDecimal subtotal = b.getTotalTableCost().add(b.getTotalProductCost()).subtract(b.getDiscountAmount());
                if (subtotal.signum() < 0) subtotal = java.math.BigDecimal.ZERO;
                taxAmount = subtotal.multiply(taxPercent).divide(new java.math.BigDecimal(100), 2, java.math.RoundingMode.HALF_UP);
                b.setFinalAmount(subtotal.add(taxAmount));
            }
        }

        billRepo.save(b);

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
                "",
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

    /**
     * Mở khóa bill (Unfinalize):
     * - Chuyển trạng thái từ Pending về Unpaid để tiếp tục tính giờ và thêm/sửa/xóa món.
     * - Không thay đổi các tổng đã khóa trước đó; UI có thể tính live-time khi bill ở Unpaid.
     * Trả về: BillDetailDTO hiện trạng sau khi mở khóa.
     * Lỗi:
     * - 404 nếu không tìm thấy bill.
     * - 409 nếu bill đã có EndTime (đã kết thúc) nên không thể unfinalize.
     */
    @PatchMapping("/bills/{billId}/unfinalize")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<BillDetailDTO> unfinalizeBill(
            @PathVariable("billId") Integer billId
    ) {
        var opt = billRepo.findById(billId); // lock
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Bill b = opt.get();
        if (b.getEndTime() != null) return ResponseEntity.status(409).build();

        b.setBillStatus("Unpaid");
        // Không thay đổi các tổng đã khóa; UI sẽ hiển thị live từ thời gian thực khi không Pending
        recomputePromotionPreviewIfAny(b);
        billRepo.save(b);

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
                "",
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

    /**
     * Lấy danh sách bàn cho staff theo club/customer hoặc tất cả.
     * - Gắn trạng thái occupied nếu bàn đang có bill active (EndTime = NULL).
     * - Chuẩn hóa status: nếu TableStatus là "Available" và có bill active → trả về "occupied", ngược lại "available".
     * Tham số:
     * - clubId | customerId (query, tùy chọn): phạm vi lọc bàn.
     * - status (query, tùy chọn): lọc theo trạng thái đã chuẩn hóa (available/occupied/... ).
     */
    @GetMapping("/tables")
    public ResponseEntity<List<TableDTO>> listTables(
            @RequestParam(value = "clubId", required = false) Integer clubId,
            @RequestParam(value = "customerId", required = false) Integer customerId,
            @RequestParam(value = "status", required = false) String status
    ) {
        List<Billiardtable> tables;
        if (clubId != null) tables = tableRepo.findByClubID_Id(clubId);
        else if (customerId != null) tables = tableRepo.findByClubID_CustomerID(customerId);
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
    /**
     * Mở bàn: tạo bill Unpaid mới nếu bàn chưa có bill active.
     * Concurrency:
     * - Khóa bản ghi bàn và truy vấn bill active theo bàn ở chế độ khóa để tránh mở trùng.
     * Lỗi:
     * - 409 nếu bàn đang có bill active.
     * Trả về: BillSummaryDTO của bill vừa mở.
     */
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
        // Avoid lazy-loading table to prevent DB schema mismatch errors
        return ResponseEntity.ok(new BillSummaryDTO(b.getId(), "", BigDecimal.ZERO, b.getBillStatus(), b.getCreatedDate()));
    }

    /**
     * Danh sách sản phẩm cho staff để thêm vào bill.
     * - Chỉ lấy các sản phẩm đang active.
     * - Ưu tiên lọc theo clubId, sau đó theo customerId; nếu không truyền thì lấy tất cả.
     */
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

    /**
     * Danh sách bill gần đây theo các tiêu chí lọc.
     * - Nếu lọc status = Paid thì sắp xếp theo EndTime desc cho "đã thanh toán"; ngược lại theo CreatedDate desc.
     * - Có thể lọc theo clubId/customerId/employeeId.
     * - Cắt danh sách theo limit (mặc định 5) sau khi lọc.
     */
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

            // Do NOT dereference table relation to avoid extra select on billiardtables
            String tableName = ""; // fallback when table metadata is unavailable
            java.time.Instant createdOrCompleted = b.getEndTime() != null ? b.getEndTime() : b.getCreatedDate();
            return new BillSummaryDTO(b.getId(), tableName, amount, b.getBillStatus(), createdOrCompleted);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // Get a full bill with items
    /**
     * Lấy chi tiết bill: gồm danh sách item và các tổng hiện có trên bill.
     * - Không fetch-join bàn để tránh issues khác DB.
     * - Dùng findWithProductByBill để lấy item kèm thông tin sản phẩm hiển thị.
     */
    @GetMapping("/bills/{billId}")
    public ResponseEntity<BillDetailDTO> getBillById(@PathVariable("billId") Integer billId) {
        // Avoid fetch-join on table to prevent querying missing columns in some DBs
        var opt = billRepo.findById(billId);
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
                "",
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
    /**
     * Thêm item vào bill đang chạy.
     * - Nếu trùng sản phẩm thì cộng dồn số lượng (qty có thể âm để trừ bớt; nếu <=0 sẽ xóa dòng).
     * - Cập nhật snapshot TotalProductCost và tính lại giảm giá xem trước nếu có promotion.
     * Ràng buộc:
     * - Chặn khi bill đang Pending (đã khóa) hoặc đã có EndTime.
     * - Nếu có employeeId, chỉ cho phép nhân viên chủ bill thao tác.
     */
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
        if (b.getBillStatus() != null && b.getBillStatus().equalsIgnoreCase("Pending")) {
            // Không cho chỉnh sửa khi bill đã khóa
            return ResponseEntity.status(409).build();
        }
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
        // Recompute promotion preview discount if a promotion is applied
        recomputePromotionPreviewIfAny(b);
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

    /**
     * Cập nhật số lượng item trong bill (qty).
     * - Nếu qty <= 0: xóa dòng item.
     * - Sau khi cập nhật: tính lại TotalProductCost và giảm giá xem trước.
     * Ràng buộc: tương tự addBillItem (không cho khi Pending/EndTime, kiểm tra quyền employee).
     */
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
        if (b.getBillStatus() != null && b.getBillStatus().equalsIgnoreCase("Pending")) {
            return ResponseEntity.status(409).build();
        }
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
            recomputePromotionPreviewIfAny(b);
            billRepo.save(b);
            return ResponseEntity.ok(new BillItemDTO(itemId, d.getProductID() != null ? d.getProductID().getId() : null, d.getProductID() != null ? d.getProductID().getProductName() : null, 0, d.getUnitPrice(), java.math.BigDecimal.ZERO));
        }
        d.setQuantity(qty);
        java.math.BigDecimal price = d.getUnitPrice() != null ? d.getUnitPrice() : (d.getProductID() != null ? d.getProductID().getPrice() : java.math.BigDecimal.ZERO);
        d.setUnitPrice(price);
        d.setSubTotal(price.multiply(new java.math.BigDecimal(qty)));
        d = billdetailRepo.save(d);
        b.setTotalProductCost(recalcProductTotal(billId));
        recomputePromotionPreviewIfAny(b);
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

    /**
     * Xóa một item khỏi bill.
     * - Sau khi xóa: cập nhật TotalProductCost và tính lại giảm giá xem trước nếu có.
     * Ràng buộc: tương tự addBillItem (không cho khi Pending/EndTime, kiểm tra quyền employee).
     */
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
        if (b.getBillStatus() != null && b.getBillStatus().equalsIgnoreCase("Pending")) {
            return ResponseEntity.status(409).build();
        }
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
        recomputePromotionPreviewIfAny(b);
        billRepo.save(b);
        return ResponseEntity.ok().build();
    }

    /**
     * Tính lại tổng tiền sản phẩm của bill từ các Billdetail.
     * - Dùng khi cần snapshot TotalProductCost để tránh sai lệch do dữ liệu cũ.
     */
    private java.math.BigDecimal recalcProductTotal(Integer billId) {
        return billdetailRepo.findByBillID_Id(billId).stream()
                .map(x -> x.getSubTotal() == null ? java.math.BigDecimal.ZERO : x.getSubTotal())
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    // Apply a promotion code to an active bill
    /**
     * Áp dụng mã khuyến mãi cho bill đang chạy.
     * - Kiểm tra mã có hiệu lực theo club, và (nếu có) ràng buộc theo customer.
     * - Gắn PromotionID vào bill, tính giảm giá xem trước (DiscountAmount) và FinalAmount (chưa gồm thuế).
     * - Chưa trừ lượt dùng; chỉ tăng UsedCount sau khi thanh toán thành công.
     * Tham số: billId (path), code (query).
     * Trả về: thông tin promotion + discount + finalPreview để UI hiển thị.
     */
    @PostMapping("/bills/{billId}/apply-promotion")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<java.util.Map<String, Object>> applyPromotion(
            @PathVariable("billId") Integer billId,
            @RequestParam("code") String code
    ) {
        var bOpt = billRepo.findById(billId);
        if (bOpt.isEmpty()) return ResponseEntity.notFound().build();
        Bill b = bOpt.get();
        Integer clubId = b.getClubID() != null ? b.getClubID().getId() : null;
        if (clubId == null) return ResponseEntity.status(400).build();

        var pOpt = promotionRepository.findValidPromotionByCodeAndClub(code, clubId, java.time.Instant.now());
        if (pOpt.isEmpty()) return ResponseEntity.status(404).build();
        Promotion p = pOpt.get();
        if (p.getCustomer() != null && p.getCustomer().getId() != null) {
            if (b.getCustomerID() == null || !p.getCustomer().getId().equals(b.getCustomerID().getId())) {
                return ResponseEntity.status(403).build();
            }
        }

        b.setPromotionID(p);

        // Compute a live preview discount against current totals
        // Prefer recalculating from bill details to reflect latest items
        java.math.BigDecimal productTotal = recalcProductTotal(b.getId());
        if (productTotal == null) productTotal = b.getTotalProductCost() == null ? java.math.BigDecimal.ZERO : b.getTotalProductCost();
        java.math.BigDecimal tableCost = java.math.BigDecimal.ZERO;
        if (b.getStartTime() != null && b.getTableID() != null && b.getTableID().getHourlyRate() != null) {
            // Mirror rounding logic from checkout: round to nearest 6 minutes
            long minutes = java.time.Duration.between(b.getStartTime(), java.time.Instant.now()).toMinutes();
            if (minutes < 0) minutes = 0;
            long rem = minutes % 6;
            long roundedMinutes = rem >= 3 ? minutes + (6 - rem) : minutes - rem;
            java.math.BigDecimal hours = new java.math.BigDecimal(roundedMinutes)
                    .divide(new java.math.BigDecimal(60), 2, java.math.RoundingMode.HALF_UP);
            tableCost = hours.multiply(b.getTableID().getHourlyRate());
        }
        java.math.BigDecimal base = tableCost.add(productTotal);
        java.math.BigDecimal discount = java.math.BigDecimal.ZERO;
        if (p.getPromotionType() == PromotionType.PERCENTAGE) {
            discount = base.multiply(p.getPromotionValue())
                    .divide(new java.math.BigDecimal(100), 2, java.math.RoundingMode.HALF_UP);
        } else {
            discount = p.getPromotionValue() == null ? java.math.BigDecimal.ZERO : p.getPromotionValue();
            if (discount.compareTo(base) > 0) discount = base;
        }
        b.setDiscountAmount(discount);
        // Preview final amount without tax; UI applies tax as needed
        java.math.BigDecimal finalPreview = base.subtract(discount);
        if (finalPreview.signum() < 0) finalPreview = java.math.BigDecimal.ZERO;
        b.setFinalAmount(finalPreview);

        billRepo.save(b);

        var resp = new java.util.HashMap<String, Object>();
        resp.put("billId", b.getId());
        resp.put("promotionId", p.getId());
        resp.put("code", p.getPromotionCode());
        resp.put("promotionType", p.getPromotionType() != null ? p.getPromotionType().name() : null);
        resp.put("promotionValue", p.getPromotionValue());
        resp.put("discount", discount);
        resp.put("finalPreview", finalPreview);
        return ResponseEntity.ok(resp);
    }

    // Complete/checkout bill: close active session and compute costs
    /**
     * Hoàn tất/Thanh toán bill (checkout):
     * - Chốt EndTime, tính TotalHours (làm tròn 6 phút), TotalTableCost.
     * - Chấp nhận productTotal/taxPercent từ client (tùy chọn). Nếu gửi danh sách items thì thay thế toàn bộ Billdetail và tính lại productTotal.
     * - Tính Discount từ Promotion nếu có (nếu không, có thể nhận discount thủ công từ payload), rồi tính FinalAmount (cộng thuế nếu truyền taxPercent).
     * - Set trạng thái Paid và lưu; sau đó tăng UsedCount của promotion nếu có.
     * Ràng buộc:
     * - Chỉ nhân viên mở bill (employeeId trùng) mới được phép complete/cancel.
     * - Nếu bill đã EndTime trước đó, chỉ trả về tóm tắt mà không tính lại.
     */
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
                    product = new java.math.BigDecimal(((Number) pt).toString());
                } else if (pt instanceof String && !((String) pt).isBlank()) {
                    try { product = new java.math.BigDecimal((String) pt); } catch (Exception ignored) {}
                }
            }

            // If a promotion has been applied to this bill, compute discount from it
            if (b.getPromotionID() != null) {
                java.math.BigDecimal base = tableCost.add(product);
                Promotion promo = b.getPromotionID();
                if (promo.getPromotionType() == PromotionType.PERCENTAGE) {
                    discount = base.multiply(promo.getPromotionValue())
                            .divide(new java.math.BigDecimal(100), 2, java.math.RoundingMode.HALF_UP);
                } else {
                    discount = promo.getPromotionValue() == null ? java.math.BigDecimal.ZERO : promo.getPromotionValue();
                    if (discount.compareTo(base) > 0) discount = base;
                }
            } else if (payload != null) {
                // Backward-compatibility: accept manual discount if no promotion applied
                Object disc = payload.get("discount");
                if (disc instanceof Number) {
                    discount = new java.math.BigDecimal(((Number) disc).toString());
                } else if (disc instanceof String && !((String) disc).isBlank()) {
                    try { discount = new java.math.BigDecimal((String) disc); } catch (Exception ignored) {}
                }
            }

            // If checkout includes items list, replace bill details accordingly and recalc product total
            if (payload != null && payload.get("items") instanceof java.util.List) {
                java.util.List<?> list = (java.util.List<?>) payload.get("items");
                // Remove existing details to replace
                billdetailRepo.deleteByBillID_Id(b.getId());

                java.math.BigDecimal productSum = java.math.BigDecimal.ZERO;
                for (Object o : list) {
                    if (!(o instanceof java.util.Map)) continue;
                    var m = (java.util.Map<?,?>) o;
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
                    taxPercent = new java.math.BigDecimal(((Number) tax).toString());
                } else if (tax instanceof String && !((String) tax).isBlank()) {
                    try { taxPercent = new java.math.BigDecimal((String) tax); } catch (Exception ignored) {}
                }
                if (taxPercent.signum() > 0) {
                    taxAmount = subtotal.multiply(taxPercent).divide(new java.math.BigDecimal(100), 2, java.math.RoundingMode.HALF_UP);
                }
            }

            java.math.BigDecimal finalAmount = subtotal.add(taxAmount);
            if (finalAmount.signum() < 0) finalAmount = java.math.BigDecimal.ZERO;
            // persist computed discount
            b.setDiscountAmount(discount);
            b.setFinalAmount(finalAmount);
            // Persist the product total if it came from client-side checkout
            b.setTotalProductCost(product);
            b.setBillStatus("Paid");

            b = billRepo.save(b);

            // increment promotion usage count after successful payment
            if (b.getPromotionID() != null) {
                try {
                    Promotion p = b.getPromotionID();
                    p.incrementUsageCount();
                    promotionRepository.save(p);
                } catch (Exception ignored) {}
            }
        }

        // Do not touch lazy table relation
        String tableName = "";
        java.math.BigDecimal amount = b.getFinalAmount() != null ? b.getFinalAmount() : java.math.BigDecimal.ZERO;
        java.time.Instant when = b.getEndTime() != null ? b.getEndTime() : b.getCreatedDate();
        return ResponseEntity.ok(new BillSummaryDTO(b.getId(), tableName, amount, b.getBillStatus(), when));
    }

    /**
     * Thống kê trong ngày cho dashboard.
     * - Khoảng thời gian: từ 00:00 hôm nay đến 00:00 ngày mai theo múi giờ hệ thống.
     * - Đếm số bill đã hoàn tất (theo EndTime) và tính doanh thu trong ngày.
     */
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

    /**
     * Xuất hóa đơn ra PDF (download attachment).
     * - Trả về PDF theo mã bill.
     */
    @GetMapping(value = "/bills/{billId}/pdf")
    public ResponseEntity<?> exportBillPdf(@PathVariable("billId") Integer billId) {
        try {
            byte[] pdf = invoicePdfService.renderBillPdf(billId);
            String filename = "bill_" + billId + ".pdf";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (IllegalArgumentException notFound) {
            return ResponseEntity.status(404)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(("Bill not found: " + billId).getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception e) {
            Throwable root = e;
            while (root.getCause() != null) root = root.getCause();
            String msg = root.getClass().getSimpleName() + (root.getMessage() != null ? (": " + root.getMessage()) : "");
            return ResponseEntity.status(500)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(("Failed to render PDF: " + msg)
                            .getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }
    }

    // Cancel an active bill (e.g., opened wrong table).
    /**
     * Hủy bill đang chạy.
     * - Chỉ cho phép nhân viên chủ bill hủy; không cho hủy nếu bill đã Paid.
     * - Set trạng thái Cancelled, đặt EndTime (nếu chưa có) và reset các tổng về 0.
     */
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
