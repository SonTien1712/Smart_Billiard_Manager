package com.BillardManagement.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EmployeeID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "ClubID", nullable = false)
    private Billardclub clubID;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "CustomerID", nullable = false)
    private Customer customerID;

    @Column(name = "EmployeeName", nullable = false)
    private String employeeName;

    @Lob
    @Column(name = "EmployeeType", nullable = false)
    private String employeeType;

    @Column(name = "PhoneNumber", length = 20)
    private String phoneNumber;

    @Column(name = "Email")
    private String email;

    @Lob
    @Column(name = "Address")
    private String address;

    @ColumnDefault("0.00")
    @Column(name = "Salary", precision = 10, scale = 2)
    private BigDecimal salary;

    @ColumnDefault("0.00")
    @Column(name = "HourlyRate", precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "DateHired")
    private Instant dateHired;

    @Column(name = "BankNumber", length = 50)
    private String bankNumber;

    @Column(name = "BankName", length = 100)
    private String bankName;

    @ColumnDefault("1")
    @Column(name = "IsActive")
    private Boolean isActive;

}