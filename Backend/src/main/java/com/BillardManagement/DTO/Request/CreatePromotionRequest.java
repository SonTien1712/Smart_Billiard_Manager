package com.BillardManagement.DTO.Request;

import com.BillardManagement.Entity.DiscountType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePromotionRequest {
    @NotNull
    private Integer clubId;

    private Integer customerId;

    @NotBlank
    @Size(min = 3, max = 255)
    private String promotionName;

    @NotBlank
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
