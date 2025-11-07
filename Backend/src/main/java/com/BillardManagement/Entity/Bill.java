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
@Table(name = "bills")
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BillID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "ClubID", nullable = false)
    private Billardclub clubID;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "CustomerID", nullable = false)
    private Customer customerID;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "TableID")
    private Billiardtable tableID;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "EmployeeID")
    private Employee employeeID;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "StartTime")
    private Instant startTime;

    @Column(name = "EndTime")
    private Instant endTime;

    @ColumnDefault("0.00")
    @Column(name = "TotalHours", precision = 4, scale = 2)
    private BigDecimal totalHours;

    @ColumnDefault("0.00")
    @Column(name = "TotalTableCost", precision = 10, scale = 2)
    private BigDecimal totalTableCost;

    @ColumnDefault("0.00")
    @Column(name = "TotalProductCost", precision = 10, scale = 2)
    private BigDecimal totalProductCost;

    @ColumnDefault("0.00")
    @Column(name = "DiscountAmount", precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "PromotionID")
    private Promotion promotionID;

    @ColumnDefault("0.00")
    @Column(name = "FinalAmount", precision = 10, scale = 2)
    private BigDecimal finalAmount;

    @Column(name = "PaymentMethod", length = 50)
    private String paymentMethod;

    @ColumnDefault("'Unpaid'")
    @Column(name = "BillStatus", length = 50)
    private String billStatus;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "CreatedDate")
    private Instant createdDate;

    private BigDecimal total_amount;
}