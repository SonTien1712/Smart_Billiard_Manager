package com.BillardManagement.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "admins")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AdminID", nullable = false)
    private Integer id;

    @Column(name = "Username", nullable = false, length = 50)
    private String username;

    @Column(name = "PasswordHash", nullable = false)
    private String passwordHash;

    @Column(name = "Email")
    private String email;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "CreatedDate")
    private Instant createdDate;

    @Column(name = "LastLogin")
    private Instant lastLogin;

    @ColumnDefault("1")
    @Column(name = "IsActive")
    private Boolean isActive;

}