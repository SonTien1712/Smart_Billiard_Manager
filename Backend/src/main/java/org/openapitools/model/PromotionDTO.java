package org.openapitools.model;

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

