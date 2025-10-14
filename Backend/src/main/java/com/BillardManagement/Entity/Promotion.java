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
@Table(name = "promotions")
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PromotionID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "ClubID", nullable = false)
    private Billardclub clubID;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "CustomerID", nullable = false)
    private Customer customerID;

    @Column(name = "PromotionName", nullable = false)
    private String promotionName;

    @Column(name = "PromotionCode", length = 50)
    private String promotionCode;

    @Lob
    @Column(name = "DiscountType", nullable = false)
    private String discountType;

    @Column(name = "DiscountValue", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "StartDate")
    private Instant startDate;

    @Column(name = "EndDate")
    private Instant endDate;

    @Lob
    @Column(name = "ApplicableTableTypes")
    private String applicableTableTypes;

    @ColumnDefault("0.00")
    @Column(name = "MinPlayTime", precision = 4, scale = 2)
    private BigDecimal minPlayTime;

    @ColumnDefault("0.00")
    @Column(name = "MinAmount", precision = 10, scale = 2)
    private BigDecimal minAmount;

    @ColumnDefault("0.00")
    @Column(name = "MaxDiscount", precision = 10, scale = 2)
    private BigDecimal maxDiscount;

    @ColumnDefault("0")
    @Column(name = "UsageLimit")
    private Integer usageLimit;

    @ColumnDefault("0")
    @Column(name = "UsedCount")
    private Integer usedCount;

    @ColumnDefault("1")
    @Column(name = "IsActive")
    private Boolean isActive;

    @Lob
    @Column(name = "Description")
    private String description;

}