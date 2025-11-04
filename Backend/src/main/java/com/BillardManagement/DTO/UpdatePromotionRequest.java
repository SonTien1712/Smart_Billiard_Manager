package com.BillardManagement.DTO;

import com.BillardManagement.Entity.PromotionType;
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

    private PromotionType promotionType;

    @DecimalMin("0.01")
    private BigDecimal promotionValue;

    private Instant startDate;
    private Instant endDate;
    // Removed legacy fields: applicableTableTypes, minPlayTime, minAmount, maxDiscount
    private Integer usageLimit;
    private Boolean isActive;
    private String description;
}

