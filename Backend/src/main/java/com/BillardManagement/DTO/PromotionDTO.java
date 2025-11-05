package com.BillardManagement.DTO;

import com.BillardManagement.Entity.DiscountType;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionDTO {
    private Integer promotionId;


    private Integer clubId;

    private Integer customerId;


    private String promotionName;


    private String promotionCode;


    private DiscountType discountType;


    private BigDecimal discountValue;


    private Instant startDate;


    private Instant endDate;

    private String applicableTableTypes;


    private BigDecimal minPlayTime;


    private BigDecimal minAmount;


    private BigDecimal maxDiscount;


    private Integer usageLimit;


    private Integer usedCount;

    private Boolean isActive;


    private String description;

    private Instant createdAt;
    private Instant updatedAt;

    // Custom validation

    public boolean isEndDateValid() {
        if (startDate == null || endDate == null) {
            return true; // Let @NotNull handle null validation
        }
        return endDate.isAfter(startDate);
    }

    public boolean isDiscountValueValid() {
        if (discountType == null || discountValue == null) {
            return true;
        }
        if (discountType == DiscountType.PERCENTAGE) {
            return discountValue.compareTo(BigDecimal.valueOf(100)) <= 0;
        }
        return true;
    }
}

