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
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProductID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "ClubID", nullable = false)
    private Billardclub clubID;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "CustomerID", nullable = false)
    private Customer customerID;

    @Column(name = "ProductName", nullable = false)
    private String productName;

    @Column(name = "Price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @ColumnDefault("0.00")
    @Column(name = "CostPrice", precision = 10, scale = 2)
    private BigDecimal costPrice;

    @Column(name = "Category", length = 100)
    private String category;

    @Lob
    @Column(name = "ProductDescription")
    private String productDescription;

    @Column(name = "ProductUrl", length = 500)
    private String productUrl;

    @ColumnDefault("1")
    @Column(name = "IsActive")
    private Boolean isActive;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "CreatedDate")
    private Instant createdDate;

}