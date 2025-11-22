package com.BillardManagement.Controller;

import com.BillardManagement.Entity.Billardclub;
import com.BillardManagement.Entity.Employee;
import com.BillardManagement.Entity.Employeeshift;
import com.BillardManagement.Repository.BilliardClubRepo;
import com.BillardManagement.Repository.EmployeeRepo;
import com.BillardManagement.Repository.EmployeeshiftRepo;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class CustomerShiftController {

    private final EmployeeRepo employeeRepo;
    private final EmployeeshiftRepo employeeshiftRepo;
    private final BilliardClubRepo clubRepo;

    // --- Staff of a club ---
    @GetMapping("/staff")
    public ResponseEntity<?> getStaffByClub(@RequestParam("clubId") Integer clubId) {
        try {
            List<Employee> list = employeeRepo.findByClubID_Id(clubId);
            List<StaffDTO> dtos = list.stream().map(e -> {
                StaffDTO d = new StaffDTO();
                d.setId(e.getId());
                d.setName(e.getEmployeeName());
                return d;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    // --- Shifts of a club in date range ---
    @GetMapping("/shifts")
    public ResponseEntity<?> getShiftsByClub(
            @RequestParam("clubId") Integer clubId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        try {
            List<Employeeshift> list = employeeshiftRepo.findByClubID_IdAndShiftDateBetween(clubId, startDate, endDate);
            List<ShiftRow> rows = list.stream().map(s -> {
                ShiftRow r = new ShiftRow();
                r.setId(s.getId());
                r.setShiftDate(s.getShiftDate());
                r.setStartTime(s.getStartTime());
                r.setEndTime(s.getEndTime());
                r.setStatus(s.getStatus());
                if (s.getEmployeeID() != null) {
                    r.setStaffId(s.getEmployeeID().getId());
                    r.setStaffName(s.getEmployeeID().getEmployeeName());
                }
                return r;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(rows);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    // --- Create a shift (assign) ---
    @PostMapping("/shifts")
    public ResponseEntity<?> createShift(@RequestBody CreateShiftRequest req) {
        try {
            if (req.getClubId() == null || req.getStaffId() == null || req.getDate() == null || req.getStartTime() == null || req.getEndTime() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing required fields");
            }

            Optional<Employee> empOpt = employeeRepo.findById(Long.valueOf(req.getStaffId()));
            if (empOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
            }
            Employee emp = empOpt.get();

            Optional<Billardclub> clubOpt = clubRepo.findById(req.getClubId());
            if (clubOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Club not found");
            }

            Employeeshift shift = new Employeeshift();
            shift.setEmployeeID(emp);
            shift.setClubID(clubOpt.get());
            // Customer from employee record to satisfy not-null relation
            shift.setCustomerID(emp.getCustomerID());
            shift.setShiftDate(LocalDate.parse(req.getDate()));
            shift.setStartTime(LocalTime.parse(req.getStartTime()));
            shift.setEndTime(LocalTime.parse(req.getEndTime()));
            shift.setSlotCode(req.getShiftCode());
            shift.setStatus(req.getStatus() != null ? req.getStatus() : "Scheduled");

            Employeeshift saved = employeeshiftRepo.save(shift);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved.getId());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @DeleteMapping("/shifts/{id}")
    public ResponseEntity<?> deleteShift(@PathVariable Integer id) {
        if (!employeeshiftRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        employeeshiftRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Data
    public static class StaffDTO {
        private Integer id;
        private String name;
    }

    @Data
    public static class ShiftRow {
        private Integer id;
        private LocalDate shiftDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private Integer staffId;
        private String staffName;
        private String status;
    }

    @Data
    public static class CreateShiftRequest {
        private Integer clubId;
        private Integer staffId;
        private String date;      // YYYY-MM-DD
        private String startTime; // HH:mm:ss
        private String endTime;   // HH:mm:ss
        private String status;    // optional, defaults to Scheduled
        private String shiftCode; // optional
    }
}

