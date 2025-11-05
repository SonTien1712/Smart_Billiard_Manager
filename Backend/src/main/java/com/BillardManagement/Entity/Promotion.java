package com.BillardManagement.Entity;

import jakarta.persistence.*;
import com.BillardManagement.Entity.converter.PromotionTypeConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "promotions", indexes = {
        @Index(name = "idx_promotion_code", columnList = "PromotionCode"),
        @Index(name = "idx_club_active", columnList = "ClubID, IsActive"),
        @Index(name = "idx_dates", columnList = "StartDate, EndDate")
})
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PromotionID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ClubID", nullable = false)
    private Billardclub club;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CustomerID")
    private Customer customer;

    @Column(name = "PromotionName", nullable = false)
    private String promotionName;

    @Column(name = "PromotionCode", length = 50, unique = true)
    private String promotionCode;

    @Convert(converter = PromotionTypeConverter.class)
    @Column(name = "DiscountType", nullable = false, length = 20)
    private PromotionType promotionType;

    @Column(name = "DiscountValue", nullable = false, precision = 10, scale = 2)
    private BigDecimal promotionValue;

    @Column(name = "StartDate", nullable = false)
    private Instant startDate;

    @Column(name = "EndDate", nullable = false)
    private Instant endDate;

    // Removed legacy constraints/filters: applicable table types, min/max requirements

    @ColumnDefault("0")
    @Column(name = "UsageLimit")
    private Integer usageLimit = 0;

    @ColumnDefault("0")
    @Column(name = "UsedCount")
    private Integer usedCount = 0;

    @ColumnDefault("true")
    @Column(name = "IsActive", nullable = false)
    private Boolean isActive = true;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    // Removed audit columns (CreatedAt, UpdatedAt) to match current DB schema

    // Business logic methods
    public boolean isValid() {
        return isActive &&
                startDate.isBefore(endDate) &&
                Instant.now().isAfter(startDate) &&
                Instant.now().isBefore(endDate);
    }

    public boolean canBeUsed() {
        return isValid() && (usageLimit == 0 || usedCount < usageLimit);
    }

    public void incrementUsageCount() {
        if (canBeUsed()) {
            this.usedCount++;
        } else {
            throw new IllegalStateException("Promotion cannot be used");
        }
    }
}



