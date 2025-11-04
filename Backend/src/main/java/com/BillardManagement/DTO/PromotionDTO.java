package com.BillardManagement.DTO;

import com.BillardManagement.Entity.PromotionType;
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

    @NotNull(message = "Club ID không được để trống")
    private Integer clubId;

    private Integer customerId;

    @NotNull(message = "Tên khuyến mãi không được để trống")
    @Size(min = 3, max = 255, message = "Tên khuyến mãi phải từ 3-255 ký tự")
    private String promotionName;

    @NotNull(message = "Mã khuyến mãi không được để trống")
    @Size(min = 3, max = 50, message = "Mã khuyến mãi phải từ 3-50 ký tự")
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "Mã khuyến mãi chỉ chứa chữ in hoa, số, gạch dưới và gạch ngang")
    private String promotionCode;

    @NotNull(message = "Loại khuyến mãi không được để trống")
    private PromotionType promotionType;

    @NotNull(message = "Giá trị khuyến mãi không được để trống")
    @DecimalMin(value = "0.01", message = "Giá trị khuyến mãi phải lớn hơn 0")
    private BigDecimal promotionValue;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant endDate;

    // Removed legacy fields: applicableTableTypes, minPlayTime, minAmount, maxDiscount

    @Min(value = 0, message = "Giới hạn sử dụng không được âm")
    private Integer usageLimit;

    @Min(value = 0, message = "Số lần đã sử dụng không được âm")
    private Integer usedCount;

    private Boolean isActive;

    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    private String description;

    // removed audit fields to match DB schema

    // Custom validation
    @AssertTrue(message = "Ngày kết thúc phải sau ngày bắt đầu")
    public boolean isEndDateValid() {
        if (startDate == null || endDate == null) {
            return true; // Let @NotNull handle null validation
        }
        return endDate.isAfter(startDate);
    }

    @AssertTrue(message = "Giá trị khuyến mãi phần trăm không được vượt quá 100")
    public boolean isPromotionValueValid() {
        if (promotionType == null || promotionValue == null) {
            return true;
        }
        if (promotionType == PromotionType.PERCENTAGE) {
            return promotionValue.compareTo(BigDecimal.valueOf(100)) <= 0;
        }
        return true;
    }
}


