// PromotionDTO.java
package com.BillardManagement.DTO;

import com.BillardManagement.Entity.DiscountType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import javax.validation.constraints.*;

public class PromotionDTO {
    private Integer promotionId;

    @NotNull(message = "Club ID is required")
    private Integer clubId;

    @NotNull(message = "Promotion name is required")
    @Size(min = 1, max = 255, message = "Promotion name must be between 1 and 255 characters")
    private String promotionName;

    @NotNull(message = "Promotion code is required")
    @Size(min = 1, max = 50, message = "Promotion code must be between 1 and 50 characters")
    private String promotionCode;

    @NotNull(message = "Discount type is required")
    @Enumerated(EnumType.STRING) // Store as string in DB
    private DiscountTypeEnum discountType;

    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal discountValue;

    @NotNull(message = "Start date is required")
    private OffsetDateTime startDate;

    @NotNull(message = "End date is required")
    private OffsetDateTime endDate;

    private Boolean isActive;

    // Getters and Setters
    public Integer getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Integer promotionId) {
        this.promotionId = promotionId;
    }

    public Integer getClubId() {
        return clubId;
    }

    public void setClubId(Integer clubId) {
        this.clubId = clubId;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public String getPromotionCode() {
        return promotionCode;
    }

    public void setPromotionCode(String promotionCode) {
        this.promotionCode = promotionCode;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountTypeEnum discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public OffsetDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(OffsetDateTime startDate) {
        this.startDate = startDate;
    }

    public OffsetDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(OffsetDateTime endDate) {
        this.endDate = endDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public enum DiscountTypeEnum {
        PERCENTAGE("Percentage"),
        FIXED_AMOUNT("FixedAmount");

        private final String value;

        DiscountTypeEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}