package com.BillardManagement.DTO;

import com.BillardManagement.Entity.DiscountType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
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

    @NotBlank(message = "Tên khuyến mãi không được để trống")
    @Size(min = 3, max = 255, message = "Tên khuyến mãi phải từ 3-255 ký tự")
    private String promotionName;

    @NotBlank(message = "Mã khuyến mãi không được để trống")
    @Size(min = 3, max = 50, message = "Mã khuyến mãi phải từ 3-50 ký tự")
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "Mã khuyến mãi chỉ chứa chữ in hoa, số, gạch dưới và gạch ngang")
    private String promotionCode;

    @NotNull(message = "Loại giảm giá không được để trống")
    private DiscountType discountType;

    @NotNull(message = "Giá trị giảm giá không được để trống")
    @DecimalMin(value = "0.01", message = "Giá trị giảm giá phải lớn hơn 0")
    @DecimalMax(value = "100.00", message = "Giá trị giảm giá không được vượt quá 100")
    private BigDecimal discountValue;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant endDate;

    private String applicableTableTypes;

    @DecimalMin(value = "0.00", message = "Thời gian chơi tối thiểu không được âm")
    private BigDecimal minPlayTime;

    @DecimalMin(value = "0.00", message = "Số tiền tối thiểu không được âm")
    private BigDecimal minAmount;

    @DecimalMin(value = "0.00", message = "Giảm giá tối đa không được âm")
    private BigDecimal maxDiscount;

    @Min(value = 0, message = "Giới hạn sử dụng không được âm")
    private Integer usageLimit;

    @Min(value = 0, message = "Số lần đã sử dụng không được âm")
    private Integer usedCount;

    private Boolean isActive;

    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    private String description;

    private Instant createdAt;
    private Instant updatedAt;

    // Custom validation
    @AssertTrue(message = "Ngày kết thúc phải sau ngày bắt đầu")
    public boolean isEndDateValid() {
        if (startDate == null || endDate == null) {
            return true; // Let @NotNull handle null validation
        }
        return endDate.isAfter(startDate);
    }

    @AssertTrue(message = "Giá trị giảm giá phần trăm không được vượt quá 100")
    public boolean isDiscountValueValid() {
        if (discountType == null || discountValue == null) {
            return true;
        }
        if (discountType == DiscountType.PERCENTAGE) {
            return discountValue.compareTo(BigDecimal.valueOf(100)) <= 0;
        }
        return true;
    }
}