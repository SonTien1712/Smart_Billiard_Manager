package com.BillardManagement.DTO;

import com.BillardManagement.Entity.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;

// Request DTO for creation
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePromotionRequest {
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
    private String description;
}
