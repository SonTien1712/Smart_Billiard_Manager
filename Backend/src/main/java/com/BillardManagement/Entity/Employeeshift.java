package com.BillardManagement.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "employeeshifts")
public class Employeeshift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ShiftID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "EmployeeID", nullable = false)
    private Employee employeeID;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "ClubID", nullable = false)
    private Billardclub clubID;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "CustomerID", nullable = false)
    private Customer customerID;

    @Column(name = "ShiftDate", nullable = false)
    private LocalDate shiftDate;

    @Column(name = "StartTime")
    private LocalTime startTime;

    @Column(name = "EndTime")
    private LocalTime endTime;

    @Column(name = "ActualStartTime")
    private Instant actualStartTime;

    @Column(name = "ActualEndTime")
    private Instant actualEndTime;

    @ColumnDefault("0.00")
    @Column(name = "HoursWorked", precision = 4, scale = 2)
    private BigDecimal hoursWorked;

    @ColumnDefault("0.00")
    @Column(name = "OvertimeHours", precision = 4, scale = 2)
    private BigDecimal overtimeHours;

    @Lob
    @Column(name = "ShiftType")
    private String shiftType;

    @ColumnDefault("'Scheduled'")
    @Column(name = "Status", length = 50)
    private String status;

}