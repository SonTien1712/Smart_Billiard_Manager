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
// Nghiệp vụ nhân viên: Bảng employeeshifts (lịch & chấm công)
// - shiftDate, startTime, endTime: thời gian kế hoạch
// - actualStartTime, actualEndTime: thời gian thực tế check-in/out
// - hoursWorked: số giờ làm thực tế (tính khi check-out)
// - status: Scheduled/Present/Completed/Absent
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
    // Ngày làm việc theo lịch
    private LocalDate shiftDate;

    @Column(name = "SlotCode")
    // Mã slot (SANG_1, CHIEU_1, DEM_1...) hỗ trợ xác định loại ca
    private String slotCode;

    @Column(name = "StartTime")
    // Giờ bắt đầu theo lịch
    private LocalTime startTime;

    @Column(name = "EndTime")
    // Giờ kết thúc theo lịch
    private LocalTime endTime;

    @Column(name = "ActualStartTime")
    // Thời điểm check-in thực tế
    private Instant actualStartTime;

    @Column(name = "ActualEndTime")
    // Thời điểm check-out thực tế
    private Instant actualEndTime;

    @ColumnDefault("0.00")
    @Column(name = "HoursWorked", precision = 4, scale = 2)
    // Số giờ làm thực tế (đơn vị giờ, có thể lẻ)
    private BigDecimal hoursWorked;

    @ColumnDefault("0.00")
    @Column(name = "OvertimeHours", precision = 4, scale = 2)
    // Số giờ tăng ca (nếu có, hiện chưa dùng)
    private BigDecimal overtimeHours;

    @Transient
    // Thuộc tính tạm để hiển thị loại ca (Sáng/Chiều/Đêm)
    private String shiftType;

    @ColumnDefault("'Scheduled'")
    @Column(name = "Status", length = 50)
    // Trạng thái ca: Scheduled/Present/Completed/Absent
    private String status;

}
