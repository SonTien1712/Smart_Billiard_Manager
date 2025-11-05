package com.BillardManagement.DTO;

import com.BillardManagement.Entity.PromotionType;
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

    @NotNull(message = "Customer ID không được để trống")
    private Integer customerId;

    @NotNull
    @Size(min = 3, max = 255)
    private String promotionName;

    @NotNull
    @Size(min = 3, max = 50)
    @Pattern(regexp = "^[A-Z0-9_-]+$")
    private String promotionCode;

    @NotNull
    private PromotionType promotionType;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal promotionValue;

    @NotNull
    private Instant startDate;

    @NotNull
    private Instant endDate;

    // Removed legacy fields: applicableTableTypes, minPlayTime, minAmount, maxDiscount
    private Integer usageLimit;
    private String description;
}

