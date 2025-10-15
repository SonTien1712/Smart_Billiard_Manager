package com.BillardManagement.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShiftDTO {
    private Integer id;
    private String slotCode;
    private LocalDate shiftDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Instant actualStartTime;
    private Instant actualEndTime;
    private BigDecimal hoursWorked;
    private BigDecimal overtimeHours;
    private String shiftType;
    private String status;
}
