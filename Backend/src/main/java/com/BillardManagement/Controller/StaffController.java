package com.BillardManagement.Controller;

import com.BillardManagement.DTO.Response.EmployeeResponse;
import com.BillardManagement.DTO.Request.EmployeeRequest;
import com.BillardManagement.DTO.Response.ShiftDTO;
import com.BillardManagement.DTO.Response.PayrollSummaryDTO;
import com.BillardManagement.Service.EmployeeService;
import com.BillardManagement.Entity.*;
import com.BillardManagement.Mapper.EmployeeMapper;
import com.BillardManagement.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final EmployeeRepo employeeRepository;
    private final EmployeeMapper employeeMapper;

    // Thêm 2 repo này
    private final BilliardClubRepo clubRepo;
    private final CustomerRepo customerRepo;

    private final EmployeeService employeeService;




    @GetMapping("/customer/{customerId}/staff/unassigned")
    public ResponseEntity<List<EmployeeResponse>> getUnassignedStaff(@PathVariable Integer customerId) {

        // Bước 1: Gọi Service để lấy danh sách Entity
        List<Employee> unassignedList = employeeService.getUnassignedEmployees(customerId);

        // Bước 2: Dùng Mapper để chuyển List<Employee> thành List<EmployeeResponse>
        List<EmployeeResponse> responses = unassignedList.stream()
                .map(employeeMapper::toResponse) // Giả sử hàm mapper của bạn tên là toResponse
                .toList(); // Hoặc .collect(Collectors.toList())

        // Bước 3: Trả về danh sách DTO
        return ResponseEntity.ok(responses);
    }


    // ==========================
    // GET STAFF BY CUSTOMER ID
    // ==========================
    @GetMapping("/{customerId}")
    public ResponseEntity<List<EmployeeResponse>> getStaffByCustomerId(@PathVariable Long customerId) {
        List<Employee> employees = employeeRepository.findByCustomerId(customerId);

        if (employees.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<EmployeeResponse> responses = employees.stream()
                .map(employeeMapper::toResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    // ==========================
    // GET ALL EMPLOYEES
    // ==========================
    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        List<EmployeeResponse> responses = employees.stream()
                .map(employeeMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    // ==========================
    // CREATE EMPLOYEE
    // ==========================
    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(@RequestBody EmployeeRequest request) {

        Employee employee = new Employee();

        employee.setEmployeeName(request.getEmployeeName());
        employee.setEmployeeType(request.getEmployeeType());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setEmail(request.getEmail());
        employee.setAddress(request.getAddress());
        employee.setDateHired(request.getDateHired());
        employee.setBankNumber(request.getBankNumber());
        employee.setBankName(request.getBankName());
        employee.setIsActive(true);

        // ✅ Logic lương theo loại nhân viên
        if ("FullTime".equalsIgnoreCase(request.getEmployeeType())) {
            employee.setSalary(request.getSalary());
            employee.setHourlyRate(null);
        } else {
            employee.setHourlyRate(request.getHourlyRate());
            employee.setSalary(null);
        }

        // Gán Club và Customer
        Billardclub club = clubRepo.findById(request.getClubId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Club với ID = " + request.getClubId()));
        Customer customer = customerRepo.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Customer với ID = " + request.getCustomerId()));

        employee.setClubID(club);
        employee.setCustomerID(customer);

        // Lưu vào DB
        Employee saved = employeeRepository.save(employee);

        return new ResponseEntity<>(employeeMapper.toResponse(saved), HttpStatus.CREATED);
    }

    // ==========================
    // UPDATE EMPLOYEE
    // ==========================
//    @PutMapping("/{id}")
//    public ResponseEntity<EmployeeResponse> updateEmployee(@PathVariable Integer id,
//                                                           @RequestBody EmployeeRequest request) {
//        Employee existingEmployee = employeeRepository.findById(id.longValue())
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với ID = " + id));
//
//        // ✅ Cập nhật các trường cơ bản
//        existingEmployee.setEmployeeName(request.getEmployeeName());
//        existingEmployee.setEmployeeType(request.getEmployeeType());
//        existingEmployee.setPhoneNumber(request.getPhoneNumber());
//        existingEmployee.setEmail(request.getEmail());
//        existingEmployee.setAddress(request.getAddress());
//        existingEmployee.setDateHired(request.getDateHired());
//        existingEmployee.setBankNumber(request.getBankNumber());
//        existingEmployee.setBankName(request.getBankName());
//        existingEmployee.setIsActive(request.getIsActive());
//
//        // ✅ Logic lương theo loại nhân viên
//        if ("FullTime".equalsIgnoreCase(request.getEmployeeType())) {
//            existingEmployee.setSalary(request.getSalary());
//            existingEmployee.setHourlyRate(null);
//        } else {
//            existingEmployee.setHourlyRate(request.getHourlyRate());
//            existingEmployee.setSalary(null);
//        }
//
//        // ✅ Gán lại Club và Customer (bắt buộc nếu nullable=false)
//        if (request.getClubId() != null) {
//            Billardclub club = clubRepo.findById(request.getClubId())
//                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Club với ID = " + request.getClubId()));
//            existingEmployee.setClubID(club);
//        }
//
//        if (request.getCustomerId() != null) {
//            Customer customer = customerRepo.findById(request.getCustomerId())
//                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Customer với ID = " + request.getCustomerId()));
//            existingEmployee.setCustomerID(customer);
//        }
//
//        // ✅ Lưu lại
//        Employee saved = employeeRepository.save(existingEmployee);
//
//        return ResponseEntity.ok(employeeMapper.toResponse(saved));
//    }
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(@PathVariable Integer id,
                                                           @RequestBody EmployeeRequest request) {

        // Xóa toàn bộ logic cũ ở đây.
        // Chỉ cần gọi EmployeeService.
        // Mọi logic, bao gồm cả @Transactional, sẽ được xử lý ở Service.

        EmployeeResponse updatedEmployee = employeeService.updateEmployee(id, request);

        return ResponseEntity.ok(updatedEmployee);
    }
    // ==========================
    // DELETE EMPLOYEE
    // ==========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Integer id) {
        employeeRepository.deleteById(id.longValue());
        return ResponseEntity.noContent().build();
    }

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

    // Nghiệp vụ: Lịch sử lương theo tháng từ lúc bắt đầu đến hiện tại
    // - Nếu truyền accountId: suy ra employeeId từ Employeeaccount và dùng DateHired làm mốc bắt đầu
    // - Nếu không: fallback lấy tháng sớm nhất có ca làm
    // - Trả về danh sách PayrollSummaryDTO theo từng tháng (startDate: đầu tháng, endDate: cuối tháng)
    @GetMapping("/payroll/history")
    public ResponseEntity<java.util.List<PayrollSummaryDTO>> payrollHistory(
            @RequestParam(value = "employeeId", required = false) Integer employeeId,
            @RequestParam(value = "accountId", required = false) Integer accountId,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        Integer resolvedEmployeeId = employeeId;
        java.math.BigDecimal employeeHourlyRate = null;
        String employmentType = "";
        java.time.Instant dateHired = null;

        if (resolvedEmployeeId == null && accountId != null) {
            Optional<Employeeaccount> acc = employeeAccountRepo.findById(accountId);
            if (acc.isPresent() && acc.get().getEmployeeID() != null) {
                resolvedEmployeeId = acc.get().getEmployeeID().getId();
                if (acc.get().getEmployeeID().getHourlyRate() != null) {
                    employeeHourlyRate = acc.get().getEmployeeID().getHourlyRate();
                }
                employmentType = acc.get().getEmployeeID().getEmployeeType();
                dateHired = acc.get().getEmployeeID().getDateHired();
            }
        }

        if (resolvedEmployeeId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Determine default range: from hired month (if available) to current month
        LocalDate today = LocalDate.now();
        if (endDate == null) {
            endDate = today.withDayOfMonth(today.lengthOfMonth());
        }
        if (startDate == null) {
            LocalDate hiredStart = null;
            if (dateHired != null) {
                hiredStart = LocalDate.ofInstant(dateHired, ZoneId.systemDefault())
                        .withDayOfMonth(1);
            }

            // Fallback: earliest shift date
            if (hiredStart == null) {
                List<Employeeshift> all = employeeshiftRepo.findByEmployeeID_Id(resolvedEmployeeId);
                java.util.Optional<LocalDate> earliest = all.stream()
                        .map(Employeeshift::getShiftDate)
                        .filter(java.util.Objects::nonNull)
                        .min(LocalDate::compareTo);
                if (earliest.isPresent()) {
                    hiredStart = earliest.get().withDayOfMonth(1);
                }
            }

            // Final fallback: current month
            if (hiredStart == null) {
                hiredStart = today.withDayOfMonth(1);
            }
            startDate = hiredStart;
        }

        // Normalize to month bounds
        LocalDate cursor = startDate.withDayOfMonth(1);
        LocalDate lastMonthStart = endDate.withDayOfMonth(1);

        java.util.List<PayrollSummaryDTO> results = new java.util.ArrayList<>();

        while (!cursor.isAfter(lastMonthStart)) {
            LocalDate monthStart = cursor;
            LocalDate monthEnd = cursor.withDayOfMonth(cursor.lengthOfMonth());

            List<Employeeshift> shifts = employeeshiftRepo
                    .findByEmployeeID_IdAndShiftDateBetween(resolvedEmployeeId, monthStart, monthEnd);

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

            // Determine hourly rate for this month
            BigDecimal monthHourlyRate = employeeHourlyRate;
            if (monthHourlyRate == null) {
                monthHourlyRate = java.util.Optional.ofNullable(shifts)
                        .flatMap(list -> list.stream().findFirst())
                        .map(s -> s.getEmployeeID() != null ? s.getEmployeeID().getHourlyRate() : null)
                        .orElse(BigDecimal.ZERO);
            }

            BigDecimal nightBonus = BigDecimal.valueOf(nightShifts * 20000L);
            BigDecimal safeHourly = monthHourlyRate == null ? BigDecimal.ZERO : monthHourlyRate;
            BigDecimal shiftPay = BigDecimal.valueOf(totalShifts)
                    .multiply(BigDecimal.valueOf(4))
                    .multiply(safeHourly);
            BigDecimal totalPay = shiftPay.add(nightBonus == null ? BigDecimal.ZERO : nightBonus);

            PayrollSummaryDTO dto = new PayrollSummaryDTO();
            dto.setStartDate(monthStart);
            dto.setEndDate(monthEnd);
            dto.setTotalHours(totalHours);
            dto.setHourlyRate(monthHourlyRate);
            dto.setTotalPay(totalPay);
            dto.setTotalShifts((int) totalShifts);
            dto.setScheduledShifts((int) scheduledShifts);
            dto.setNightShifts((int) nightShifts);
            dto.setEmploymentType(employmentType);
            dto.setNightBonus(nightBonus);
            results.add(dto);

            cursor = cursor.plusMonths(1);
        }

        return ResponseEntity.ok(results);
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
