package com.BillardManagement.DTO.Response;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDTO {

    private Integer id;
    private Integer clubId;
    private String clubName;
    private String name;
    private BigDecimal price;
    private BigDecimal costPrice;
    private String category;
    private String description;
    private String productUrl;
    private Boolean active;
    private Instant createdDate;

    // Calculated field
    private BigDecimal profitMargin;

    public void calculateProfitMargin() {
        if (price != null && costPrice != null && costPrice.compareTo(BigDecimal.ZERO) > 0) {
            profitMargin = price.subtract(costPrice)
                    .divide(costPrice, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
    }
}