package com.BillardManagement.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

// Tổng hợp lương/thống kê theo khoảng thời gian cho nhân viên
// - totalHours: tổng số giờ làm thực tế (cộng HoursWorked)
// - totalShifts: số công đã làm (trừ Absent)
// - scheduledShifts: tổng số ca theo lịch để hiển thị a/b
// - totalPay = totalShifts * 4 * hourlyRate + nightBonus
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PayrollSummaryDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalHours;     // tổng giờ thực tế
    private BigDecimal hourlyRate;     // lương theo giờ
    private BigDecimal totalPay;       // tiền lương đã tính theo quy tắc

    private Integer totalShifts;       // tổng công đã làm
    private Integer scheduledShifts;   // tổng ca theo lịch
    private Integer nightShifts;       // số công đêm
    private String employmentType;     // Full/Part time (thông tin tham khảo)
    private BigDecimal nightBonus;     // công đêm * 20,000
}
