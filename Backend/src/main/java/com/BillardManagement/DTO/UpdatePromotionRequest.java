package com.BillardManagement.DTO;

import com.BillardManagement.Entity.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;

// Request DTO for update (all fields optional)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePromotionRequest {
    @Size(min = 3, max = 255)
    private String promotionName;

    @Size(min = 3, max = 50)
    @Pattern(regexp = "^[A-Z0-9_-]+$")
    private String promotionCode;

    private DiscountType discountType;

    @DecimalMin("0.01")
    private BigDecimal discountValue;

    private Instant startDate;
    private Instant endDate;
    private String applicableTableTypes;
    private BigDecimal minPlayTime;
    private BigDecimal minAmount;
    private BigDecimal maxDiscount;
    private Integer usageLimit;
    private Boolean isActive;
    private String description;
}
