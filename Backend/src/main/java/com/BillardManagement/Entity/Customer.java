package com.BillardManagement.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CustomerID", nullable = false)
    private Integer id;

    @Column(name = "CustomerName", nullable = false)
    private String customerName;

    @Column(name = "PhoneNumber", length = 20)
    private String phoneNumber;

    @Column(name = "Email")
    private String email;

    @Column(name = "Password", nullable = false)
    private String password;

    @Lob
    @Column(name = "Address")
    private String address;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "DateJoined")
    private Instant dateJoined;

    @Column(name = "ExpiryDate")
    private LocalDate expiryDate;

    @Column(name = "GoogleId")
    private String googleId;

    @ColumnDefault("1")
    @Column(name = "IsActive")
    private Boolean isActive;

}