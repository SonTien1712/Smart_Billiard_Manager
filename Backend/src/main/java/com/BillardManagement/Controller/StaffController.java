package com.BillardManagement.Controller;

import com.BillardManagement.DTO.Response.ShiftDTO;
import com.BillardManagement.DTO.Response.PayrollSummaryDTO;
import com.BillardManagement.Entity.Employeeaccount;
import com.BillardManagement.Entity.Employeeshift;
import com.BillardManagement.Repository.EmployeeAccountRepo;
import com.BillardManagement.Repository.EmployeeshiftRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class StaffController {

    private final EmployeeshiftRepo employeeshiftRepo;
    private final EmployeeAccountRepo employeeAccountRepo;

    // Nghiệp vụ: Lấy lịch làm việc theo tuần cho nhân viên
    // - Tự suy ra employeeId từ accountId nếu cung cấp accountId (đăng nhập)
    // - Mặc định phạm vi là tuần hiện tại nếu không truyền startDate/endDate
    // - Tự động đánh vắng (Absent) nếu quá 5 phút sau giờ bắt đầu mà chưa check-in
    // - Trả về danh sách ShiftDTO cho frontend hiển thị lịch
    @GetMapping("/schedule")
    public ResponseEntity<List<ShiftDTO>> getSchedule(
            @RequestParam(value = "employeeId", required = false) Integer employeeId,
            @RequestParam(value = "accountId", required = false) Integer accountId,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        Integer resolvedEmployeeId = employeeId;
        if (resolvedEmployeeId == null && accountId != null) {
            Optional<Employeeaccount> acc = employeeAccountRepo.findById(accountId);
            if (acc.isPresent() && acc.get().getEmployeeID() != null) {
                resolvedEmployeeId = acc.get().getEmployeeID().getId();
            }
        }

        if (resolvedEmployeeId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // default range: current week
        LocalDate today = LocalDate.now();
        if (startDate == null || endDate == null) {
            startDate = today.minusDays(today.getDayOfWeek().getValue() - 1);
            endDate = startDate.plusDays(6);
        }

        List<Employeeshift> shifts = employeeshiftRepo
                .findByEmployeeID_IdAndShiftDateBetween(resolvedEmployeeId, startDate, endDate);

        // Auto-mark absent if late > 5 minutes without check-in.
        // Also revert premature Absent to Scheduled if not yet in grace window.
        Instant now = Instant.now();
        boolean changed = false;
        for (Employeeshift s : shifts) {
            String st = s.getStatus();
            boolean isScheduledLike = (st == null || st.isBlank() || st.equalsIgnoreCase("Scheduled"));
            Instant shiftStart = toShiftStartInstant(s);

            // Late without check-in -> mark Absent
            if (s.getActualStartTime() == null && isScheduledLike) {
                if (shiftStart != null && now.isAfter(shiftStart.plus(5, ChronoUnit.MINUTES))) {
                    s.setStatus("Absent");
                    employeeshiftRepo.save(s);
                    changed = true;
                }
            }

            // If previously marked Absent but it's not yet time, revert to Scheduled
            if (s.getActualStartTime() == null && st != null && st.equalsIgnoreCase("Absent")) {
                if (shiftStart != null && now.isBefore(shiftStart.plus(5, ChronoUnit.MINUTES))) {
                    s.setStatus("Scheduled");
                    employeeshiftRepo.save(s);
                    changed = true;
                }
            }
        }
        if (changed) {
            shifts = employeeshiftRepo.findByEmployeeID_IdAndShiftDateBetween(resolvedEmployeeId, startDate, endDate);
        }

        List<ShiftDTO> dtos = shifts.stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Nghiệp vụ: Check-in ca làm việc
    // - Chặn việc một nhân viên có 2 ca đang hoạt động (đã start nhưng chưa end)
    // - Cho phép check-in từ 15 phút trước giờ bắt đầu đến 5 phút sau giờ bắt đầu
    // - Nếu trễ hơn +5 phút mà chưa check-in: đánh trạng thái Absent và trả lỗi
    // - Check-in thành công sẽ lưu thời điểm thực tế và đặt trạng thái Present
    @PostMapping("/attendance/check-in")
    public ResponseEntity<ShiftDTO> checkIn(@RequestBody CheckInRequest request) {
        Optional<Employeeshift> opt = employeeshiftRepo.findById(request.getShiftId());
        if (opt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        Employeeshift shift = opt.get();

        // Prevent multiple active check-ins
        List<Employeeshift> allOfEmp = employeeshiftRepo.findByEmployeeID_Id(shift.getEmployeeID().getId());
        boolean hasActive = allOfEmp.stream().anyMatch(x -> x.getActualStartTime() != null && x.getActualEndTime() == null);
        if (hasActive) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Instant now = Instant.now();
        Instant shiftStart = toShiftStartInstant(shift);
        if (shiftStart != null) {
            // Allow check-in from 15 minutes before to 5 minutes after start
            Instant earliest = shiftStart.minus(15, ChronoUnit.MINUTES);
            Instant latest = shiftStart.plus(5, ChronoUnit.MINUTES);

            // Too early
            if (now.isBefore(earliest)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            // Too late -> mark absent
            if (now.isAfter(latest) && shift.getActualStartTime() == null) {
                shift.setStatus("Absent");
                employeeshiftRepo.save(shift);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(toDTO(shift));
            }
        }

        if (shift.getActualStartTime() == null) {
            shift.setActualStartTime(now);
            shift.setStatus("Present");
            employeeshiftRepo.save(shift);
        }
        return ResponseEntity.ok(toDTO(shift));
    }

    // Nghiệp vụ: Check-out ca làm việc
    // - Yêu cầu ca đã được check-in trước đó
    // - Lưu thời điểm kết thúc thực tế, tính số giờ làm = Duration(actualStart, actualEnd)
    // - Đặt trạng thái Completed sau khi tính giờ
    @PatchMapping("/attendance/{shiftId}/check-out")
    public ResponseEntity<ShiftDTO> checkOut(@PathVariable Integer shiftId) {
        Optional<Employeeshift> opt = employeeshiftRepo.findById(shiftId);
        if (opt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        Employeeshift shift = opt.get();
        if (shift.getActualStartTime() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (shift.getActualEndTime() == null) {
            shift.setActualEndTime(Instant.now());
            // compute hoursWorked
            BigDecimal hours = BigDecimal.valueOf(
                    Duration.between(shift.getActualStartTime(), shift.getActualEndTime()).toMinutes() / 60.0
            );
            shift.setHoursWorked(hours);
            shift.setStatus("Completed");
            employeeshiftRepo.save(shift);
        }
        return ResponseEntity.ok(toDTO(shift));
    }

    // Nghiệp vụ: Tổng hợp lương theo tháng/khoảng thời gian
    // - Xác định nhân viên từ employeeId hoặc accountId
    // - Tính các chỉ số: tổng công (trừ Absent), công đêm, tổng giờ thực tế
    // - NightBonus = công đêm * 20.000
    // - TotalPay = tổng công * 4 giờ * HourlyRate + NightBonus
    // - scheduledShifts: tổng số ca theo lịch trong khoảng, dùng để hiển thị a/b
    @GetMapping("/payroll/summary")
    public ResponseEntity<PayrollSummaryDTO> payrollSummary(
            @RequestParam(value = "employeeId", required = false) Integer employeeId,
            @RequestParam(value = "accountId", required = false) Integer accountId,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        Integer resolvedEmployeeId = employeeId;
        if (resolvedEmployeeId == null && accountId != null) {
            Optional<Employeeaccount> acc = employeeAccountRepo.findById(accountId);
            if (acc.isPresent() && acc.get().getEmployeeID() != null) {
                resolvedEmployeeId = acc.get().getEmployeeID().getId();
            }
        }

        if (resolvedEmployeeId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // default current month
        LocalDate today = LocalDate.now();
        if (startDate == null || endDate == null) {
            startDate = today.withDayOfMonth(1);
            endDate = today.withDayOfMonth(today.lengthOfMonth());
        }

        List<Employeeshift> shifts = employeeshiftRepo
                .findByEmployeeID_IdAndShiftDateBetween(resolvedEmployeeId, startDate, endDate);

        // Calculate công and night công
        long scheduledShifts = shifts.size();
        long totalShifts = shifts.stream()
                .filter(s -> s.getStatus() != null && !s.getStatus().equalsIgnoreCase("Absent"))
                .count();
        long nightShifts = shifts.stream()
                .filter(s -> s.getStatus() != null && !s.getStatus().equalsIgnoreCase("Absent"))
                .filter(this::isNightShift)
                .count();

        BigDecimal totalHours = shifts.stream()
                .map(s -> s.getHoursWorked() == null ? BigDecimal.ZERO : s.getHoursWorked())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal hourlyRate = Optional.ofNullable(shifts)
                .flatMap(list -> list.stream().findFirst())
                .map(s -> s.getEmployeeID() != null ? s.getEmployeeID().getHourlyRate() : null)
                .orElse(BigDecimal.ZERO);

        BigDecimal nightBonus = BigDecimal.valueOf(nightShifts * 20000L);

        // Determine employment type and salary
        String employmentType = Optional.ofNullable(shifts)
                .flatMap(list -> list.stream().findFirst())
                .map(s -> s.getEmployeeID() != null ? s.getEmployeeID().getEmployeeType() : null)
                .orElse("");

        // Total pay = totalShifts x 4h x hourlyRate + nightBonus
        BigDecimal safeHourly = hourlyRate == null ? BigDecimal.ZERO : hourlyRate;
        BigDecimal shiftPay = BigDecimal.valueOf(totalShifts)
                .multiply(BigDecimal.valueOf(4))
                .multiply(safeHourly);
        BigDecimal totalPay = shiftPay.add(nightBonus == null ? BigDecimal.ZERO : nightBonus);

        PayrollSummaryDTO dto = new PayrollSummaryDTO();
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        dto.setTotalHours(totalHours);
        dto.setHourlyRate(hourlyRate);
        dto.setTotalPay(totalPay);
        dto.setTotalShifts((int) totalShifts);
        dto.setScheduledShifts((int) scheduledShifts);
        dto.setNightShifts((int) nightShifts);
        dto.setEmploymentType(employmentType);
        dto.setNightBonus(nightBonus);
        return ResponseEntity.ok(dto);
    }

    // Chuyển đổi thực thể Employeeshift sang ShiftDTO cho frontend
    // - Suy ra loại ca (Sáng/Chiều/Đêm) từ slotCode nếu có
    // - Suy ra trạng thái nếu DB chưa set: Scheduled/Present/Completed theo actual times
    private ShiftDTO toDTO(Employeeshift e) {
        ShiftDTO d = new ShiftDTO();
        d.setId(e.getId());
        d.setSlotCode(e.getSlotCode());
        d.setShiftDate(e.getShiftDate());
        d.setStartTime(e.getStartTime());
        d.setEndTime(e.getEndTime());
        d.setActualStartTime(e.getActualStartTime());
        d.setActualEndTime(e.getActualEndTime());
        d.setHoursWorked(e.getHoursWorked());
        d.setOvertimeHours(e.getOvertimeHours());
        // Derive human-readable shift type from slotCode if available
        String derivedType = null;
        if (e.getSlotCode() != null) {
            String sc = e.getSlotCode().toUpperCase();
            if (sc.startsWith("SANG")) derivedType = "Sáng";
            else if (sc.startsWith("CHIEU")) derivedType = "Chiều";
            else if (sc.startsWith("DEM")) derivedType = "Đêm";
        }
        d.setShiftType(derivedType);
        // Derive status if null to reflect DB rows with actual times filled
        String status = e.getStatus();
        if (status == null || status.isBlank()) {
            if (e.getActualStartTime() != null && e.getActualEndTime() != null) {
                status = "Completed";
            } else if (e.getActualStartTime() != null) {
                status = "Present";
            } else {
                status = "Scheduled";
            }
        }
        d.setStatus(status);
        return d;
    }

    // Xác định ca đêm dựa trên giờ bắt đầu (>=22:00 hoặc <06:00)
    private boolean isNightShift(Employeeshift s) {
        if (s.getStartTime() == null) return false;
        int startMin = s.getStartTime().getHour() * 60 + s.getStartTime().getMinute();
        return startMin >= 22 * 60 || startMin < 6 * 60;
    }

    // Tạo mốc thời gian Instant cho thời điểm bắt đầu ca theo ngày + giờ bắt đầu
    private Instant toShiftStartInstant(Employeeshift s) {
        if (s.getShiftDate() == null || s.getStartTime() == null) return null;
        LocalDate date = s.getShiftDate();
        // Nếu là ca đêm (Đêm/DEM) và bắt đầu trước 06:00, coi như thuộc ngày kế tiếp
        String code = s.getSlotCode();
        int startHour = s.getStartTime().getHour();
        if (startHour < 6) {
            String n = code == null ? "" : code.trim();
            String lower = n.toLowerCase();
            if (lower.startsWith("dem") || lower.startsWith("đêm")) {
                date = date.plusDays(1);
            }
        }
        LocalDateTime ldt = LocalDateTime.of(date, s.getStartTime());
        return ldt.atZone(ZoneId.systemDefault()).toInstant();
    }

    // Payload nhận khi check-in: chỉ cần shiftId
    public static class CheckInRequest {
        private Integer shiftId;
        public Integer getShiftId() { return shiftId; }
        public void setShiftId(Integer shiftId) { this.shiftId = shiftId; }
    }
}
