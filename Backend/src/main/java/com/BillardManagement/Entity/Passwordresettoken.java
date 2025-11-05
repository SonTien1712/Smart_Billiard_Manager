package com.BillardManagement.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "passwordresettokens")
public class Passwordresettoken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TokenID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserID", nullable = false)
    private Customer userID;

    @Column(name = "Token", nullable = false)
    private String token;

    @Column(name = "ExpiryDate", nullable = false)
    private Instant expiryDate;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "CreatedDate")
    private Instant createdDate;

}