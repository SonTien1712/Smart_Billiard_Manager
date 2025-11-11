package com.BillardManagement.Service.Impl;

import com.BillardManagement.Entity.Bill;
import com.BillardManagement.Entity.Billdetail;
import com.BillardManagement.Repository.BillRepo;
import com.BillardManagement.Repository.BilldetailRepo;
import com.BillardManagement.Service.InvoicePdfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoicePdfServiceImpl implements InvoicePdfService {

    private final BillRepo billRepo;
    private final BilldetailRepo billdetailRepo;

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public byte[] renderBillPdf(Integer billId) {
        Bill bill = billRepo.findViewDeepById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found: " + billId));
        List<Billdetail> details = billdetailRepo.findWithProductByBill(billId);

        String html = buildHtml(bill, details);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            // Register Unicode-capable fonts for Vietnamese text if available
            registerFonts(builder);
            builder.withHtmlContent(html, null);
            builder.useFastMode();
            builder.toStream(out);
            builder.run();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Render PDF failed for billId={}: {}", billId, e.getMessage(), e);
            throw new RuntimeException("Failed to render bill PDF", e);
        }
    }

    private String buildHtml(Bill b, List<Billdetail> items) {
        String clubName = b.getClubID() != null ? safe(b.getClubID().getClubName()) : "";
        String clubPhone = b.getClubID() != null ? safe(nullToEmpty(b.getClubID().getPhoneNumber())) : "";
        String clubAddr = b.getClubID() != null ? safe(nullToEmpty(b.getClubID().getAddress())) : "";

        String tableName = b.getTableID() != null ? safe(nullToEmpty(b.getTableID().getTableName())) : "";
        String staffName = (b.getEmployeeID() != null && b.getEmployeeID().getEmployeeName() != null)
                ? safe(b.getEmployeeID().getEmployeeName()) : "";

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                .withZone(ZoneId.systemDefault());
        String start = b.getStartTime() != null ? df.format(b.getStartTime()) : "";
        String end = b.getEndTime() != null ? df.format(b.getEndTime()) : "";

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'/><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>");
        sb.append("<style>");
        sb.append("body{font-family:'Noto Sans','DejaVu Sans','Arial Unicode MS',Arial,Helvetica,sans-serif;font-size:12px;color:#222;margin:24px;}");
        sb.append(".header{border-bottom:1px solid #ddd;padding-bottom:8px;margin-bottom:12px;}");
        sb.append(".title{font-size:18px;font-weight:bold;margin:0 0 4px 0;}");
        sb.append(".club{font-size:12px;color:#555;}");
        sb.append(".meta{margin:12px 0;}");
        sb.append("table{width:100%;border-collapse:collapse;margin-top:8px;}");
        sb.append("th,td{border:1px solid #e5e5e5;padding:6px;text-align:left;}");
        sb.append("th{background:#f8f8f8;}");
        sb.append(".right{text-align:right;}");
        sb.append(".totals{margin-top:12px;float:right;width:50%;}");
        sb.append(".totals td{border:none;padding:4px;}");
        sb.append(".totals .label{text-align:left;}");
        sb.append(".totals .value{text-align:right;}");
        sb.append(".footer{margin-top:24px;font-size:11px;color:#777;border-top:1px solid #ddd;padding-top:8px;}");
        sb.append("</style></head><body>");

        // Header
        sb.append("<div class='header'>");
        sb.append("<div class='title'>Hóa đơn thanh toán</div>");
        sb.append("<div class='club'>").append(clubName).append("</div>");
        if (!clubPhone.isBlank()) sb.append("<div class='club'>Điện thoại: ").append(clubPhone).append("</div>");
        if (!clubAddr.isBlank()) sb.append("<div class='club'>Địa chỉ: ").append(clubAddr).append("</div>");
        sb.append("</div>");

        // Meta
        sb.append("<div class='meta'>");
        sb.append("<div>Mã hóa đơn: ").append(b.getId()).append("</div>");
        sb.append("<div>Trạng thái: ").append(safe(nullToEmpty(b.getBillStatus()))).append("</div>");
        sb.append("<div>Giờ bắt đầu: ").append(start).append("</div>");
        if (b.getEndTime() != null) sb.append("<div>Giờ kết thúc: ").append(end).append("</div>");
        if (!tableName.isBlank()) sb.append("<div>Bàn: ").append(tableName).append("</div>");
        if (!staffName.isBlank()) sb.append("<div>Thu ngân: ").append(staffName).append("</div>");
        sb.append("</div>");

        // Items table
        sb.append("<table>");
        sb.append("<thead><tr><th>Sản phẩm/Dịch vụ</th><th class='right'>SL</th><th class='right'>Đơn giá</th><th class='right'>Thành tiền</th></tr></thead><tbody>");

        // Table time cost as service line
        if (b.getTotalHours() != null && b.getTotalTableCost() != null && b.getTableID() != null) {
            String svcName = "Giờ bàn - " + safe(nullToEmpty(b.getTableID().getTableName())) +
                    " (" + fmt(b.getTotalHours()) + " giờ)";
            sb.append("<tr>")
                    .append("<td>").append(svcName).append("</td>")
                    .append("<td class='right'>1</td>")
                    .append("<td class='right'>").append(fmt(b.getTableID().getHourlyRate())).append("</td>")
                    .append("<td class='right'>").append(fmt(b.getTotalTableCost())).append("</td>")
                    .append("</tr>");
        }

        for (Billdetail d : items) {
            String name = d.getProductID() != null ? nullToEmpty(d.getProductID().getProductName()) : "Sản phẩm";
            sb.append("<tr>")
                    .append("<td>").append(safe(name)).append("</td>")
                    .append("<td class='right'>").append(String.valueOf(d.getQuantity() == null ? 0 : d.getQuantity())).append("</td>")
                    .append("<td class='right'>").append(fmt(d.getUnitPrice())).append("</td>")
                    .append("<td class='right'>").append(fmt(d.getSubTotal())).append("</td>")
                    .append("</tr>");
        }

        sb.append("</tbody></table>");

        // Totals
        BigDecimal tableCost = nz(b.getTotalTableCost());
        BigDecimal productCost = nz(b.getTotalProductCost());
        BigDecimal discount = nz(b.getDiscountAmount());
        BigDecimal finalAmount = nz(b.getFinalAmount());

        sb.append("<table class='totals'>");
        sb.append(row("Tổng giờ bàn", fmt(tableCost)));
        sb.append(row("Tổng sản phẩm", fmt(productCost)));
        if (discount.signum() > 0) sb.append(row("Giảm giá", "-" + fmt(discount)));
        sb.append("<tr><td class='label'><strong>Thanh toán</strong></td><td class='value'><strong>")
                .append(fmt(finalAmount.signum() > 0 ? finalAmount : tableCost.add(productCost).subtract(discount)))
                .append("</strong></td></tr>");
        sb.append("</table>");

        sb.append("<div class='footer'>Cảm ơn quý khách! Hẹn gặp lại.</div>");
        sb.append("</body></html>");
        return sb.toString();
    }

    private void registerFonts(PdfRendererBuilder builder) {
        // Register via InputStream supplier (works both in dev and packaged jar)
        try {
            ClassPathResource regular = new ClassPathResource("fonts/NotoSans-Regular.ttf");
            if (regular.exists()) {
                builder.useFont(() -> {
                    try { return regular.getInputStream(); }
                    catch (java.io.IOException ioe) { throw new RuntimeException(ioe); }
                }, "Noto Sans");
            }
        } catch (Exception e) {
            log.warn("Could not load NotoSans-Regular.ttf: {}", e.getMessage());
        }
        try {
            ClassPathResource bold = new ClassPathResource("fonts/NotoSans-Bold.ttf");
            if (bold.exists()) {
                builder.useFont(() -> {
                    try { return bold.getInputStream(); }
                    catch (java.io.IOException ioe) { throw new RuntimeException(ioe); }
                }, "Noto Sans", 700, BaseRendererBuilder.FontStyle.NORMAL, true);
            }
        } catch (Exception e) {
            log.warn("Could not load NotoSans-Bold.ttf: {}", e.getMessage());
        }
    }

    private String row(String label, String value) {
        return new StringBuilder()
                .append("<tr><td class='label'>").append(safe(label))
                .append("</td><td class='value'>").append(safe(value))
                .append("</td></tr>")
                .toString();
    }

    private BigDecimal nz(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }
    private String fmt(BigDecimal v) { return (v == null ? BigDecimal.ZERO : v).setScale(2, RoundingMode.HALF_UP).toPlainString(); }
    private String nullToEmpty(String s) { return s == null ? "" : s; }
    private String safe(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
