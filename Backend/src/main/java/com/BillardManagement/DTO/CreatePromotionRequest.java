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
    @NotNull
    private Integer clubId;

    private Integer customerId;

    @NotNull
    @Size(min = 3, max = 255)
    private String promotionName;

    @NotNull
    @Size(min = 3, max = 50)
    @Pattern(regexp = "^[A-Z0-9_-]+$")
    private String promotionCode;

    @NotNull
    private DiscountType discountType;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal discountValue;

    @NotNull
    private Instant startDate;

    @NotNull
    private Instant endDate;

    private String applicableTableTypes;
    private BigDecimal minPlayTime;
    private BigDecimal minAmount;
    private BigDecimal maxDiscount;
    private Integer usageLimit;
    private String description;
}
