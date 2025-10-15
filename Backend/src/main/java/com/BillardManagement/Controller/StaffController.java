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

        // Auto-mark absent if late > 5 minutes without check-in
        Instant now = Instant.now();
        boolean changed = false;
        for (Employeeshift s : shifts) {
            if (s.getActualStartTime() == null && s.getStatus() != null && s.getStatus().equalsIgnoreCase("Scheduled")) {
                Instant shiftStart = toShiftStartInstant(s);
                if (shiftStart != null && now.isAfter(shiftStart.plus(5, ChronoUnit.MINUTES))) {
                    s.setStatus("Absent");
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
            // cannot check in before shift
            if (now.isBefore(shiftStart)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            // mark absent if > 5 minutes late
            if (now.isAfter(shiftStart.plus(5, ChronoUnit.MINUTES)) && shift.getActualStartTime() == null) {
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
        BigDecimal baseSalary = Optional.ofNullable(shifts)
                .flatMap(list -> list.stream().findFirst())
                .map(s -> s.getEmployeeID() != null ? s.getEmployeeID().getSalary() : null)
                .orElse(BigDecimal.ZERO);

        BigDecimal totalPay;
        if (employmentType != null && employmentType.toLowerCase().contains("full")) {
            // Full time: 50 công => 100% salary, prorated, + night bonus
            BigDecimal ratio = BigDecimal.valueOf(Math.min(totalShifts, 50L)).divide(BigDecimal.valueOf(50L), 4, java.math.RoundingMode.HALF_UP);
            totalPay = baseSalary.multiply(ratio).add(nightBonus);
        } else {
            // Part time: số công x 4 x hourlyRate + night bonus
            BigDecimal ptPay = BigDecimal.valueOf(totalShifts).multiply(BigDecimal.valueOf(4)).multiply(hourlyRate);
            totalPay = ptPay.add(nightBonus);
        }

        PayrollSummaryDTO dto = new PayrollSummaryDTO();
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        dto.setTotalHours(totalHours);
        dto.setHourlyRate(hourlyRate);
        dto.setTotalPay(totalPay);
        dto.setTotalShifts((int) totalShifts);
        dto.setNightShifts((int) nightShifts);
        dto.setEmploymentType(employmentType);
        dto.setBaseSalary(baseSalary);
        dto.setNightBonus(nightBonus);
        return ResponseEntity.ok(dto);
    }

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

    private boolean isNightShift(Employeeshift s) {
        if (s.getStartTime() == null) return false;
        int startMin = s.getStartTime().getHour() * 60 + s.getStartTime().getMinute();
        return startMin >= 22 * 60 || startMin < 6 * 60;
    }

    private Instant toShiftStartInstant(Employeeshift s) {
        if (s.getShiftDate() == null || s.getStartTime() == null) return null;
        LocalDateTime ldt = LocalDateTime.of(s.getShiftDate(), s.getStartTime());
        return ldt.atZone(ZoneId.systemDefault()).toInstant();
    }

    public static class CheckInRequest {
        private Integer shiftId;
        public Integer getShiftId() { return shiftId; }
        public void setShiftId(Integer shiftId) { this.shiftId = shiftId; }
    }
}
