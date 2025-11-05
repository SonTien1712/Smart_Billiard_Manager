package com.BillardManagement.DTO.Request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDTO {
    private Integer clubId;
    private String name;
    private BigDecimal price;
    private BigDecimal costPrice;
    private String category;
    private String description;
    private String productUrl;
    private Boolean active;
    /**
     * Phương thức helper để convert sang Entity (tùy chọn)
     * Nhưng thường tốt hơn là để logic này ở Service layer
     */
    // Không nên có method toEntity() ở RequestDTO vì cần Customer và Club từ DB
}