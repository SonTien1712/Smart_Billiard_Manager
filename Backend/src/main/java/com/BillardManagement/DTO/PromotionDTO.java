package com.BillardManagement.DTO;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;

public class PromotionDTO {
    private Integer promotionId;
    private Integer clubId;
    private String promotionName;
    private String promotionCode;
    private DiscountTypeEnum discountType;
    private BigDecimal discountValue;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private Boolean isActive;

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

    public DiscountTypeEnum getDiscountType() {
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

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getIsActive() {
        return isActive;
    }
    public Boolean setIsActive(Boolean isActive) {
        this.isActive = isActive;
        return isActive;
    }

    public enum DiscountTypeEnum {
        PERCENTAGE("Percentage"),
        FIXED_AMOUNT("FixedAmount");
        private String value;
        DiscountTypeEnum(String value) { this.value = value; }
        public String getValue() { return value; }
        @Override public String toString() { return value; }
    }

    // Getters and setters for all fields
    // ...existing code...
}

