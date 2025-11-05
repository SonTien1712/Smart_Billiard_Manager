package com.BillardManagement.DTO.Response;

import com.BillardManagement.Entity.PromotionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.BillardManagement.DTO.PromotionDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionView {
    private Integer id;

    private String code;

    private String name;

    private String promotionType; // "percentage" or "fixed"

    private BigDecimal promotionValue;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    private Instant startDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    private Instant endDate;

    private String status; // "active" or "inactive"

    private Integer usageCount;

    // Static factory method để convert từ PromotionDTO sang PromotionView
    public static PromotionView fromDTO(PromotionDTO dto) {
        return PromotionView.builder()
                .id(dto.getPromotionId())
                .code(dto.getPromotionCode())
                .name(dto.getPromotionName())
                .promotionType(mapPromotionType(dto.getPromotionType()))
                .promotionValue(dto.getPromotionValue())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .status(dto.getIsActive() != null && dto.getIsActive() ? "active" : "inactive")
                .usageCount(dto.getUsedCount() != null ? dto.getUsedCount() : 0)
                .build();
    }

    // Helper method để map PromotionType enum sang string
    private static String mapPromotionType(PromotionType type) {
        if (type == null) return "percentage";
        return type == PromotionType.PERCENTAGE ? "percentage" : "fixed";
    }
}



