package com.BillardManagement.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "employeeaccounts")
public class Employeeaccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID", nullable = false)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "EmployeeID")
    private Employee employeeID;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "ClubID", nullable = false)
    private Billardclub clubID;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "CustomerID", nullable = false)
    private Customer customerID;

    @Column(name = "Username", nullable = false, length = 50)
    private String username;

    @Column(name = "PasswordHash", nullable = false)
    private String passwordHash;

    @Column(name = "LastLogin")
    private Instant lastLogin;

    @ColumnDefault("1")
    @Column(name = "IsActive")
    private Boolean isActive;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "CreatedDate")
    private Instant createdDate;

}