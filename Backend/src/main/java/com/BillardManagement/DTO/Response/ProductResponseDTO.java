package com.BillardManagement.DTO.Response;

import com.BillardManagement.Entity.Product;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO {
    private Integer id;              // ✅ Frontend: product.id
    private String name;             // ✅ Frontend: product.name
    private String category;         // ✅ Frontend: product.category
    private BigDecimal price;        // ✅ Frontend: product.price
    private BigDecimal costPrice;    // ✅ Frontend: product.costPrice
    private String description;      // ✅ Frontend: product.description
    private String productUrl;       // ✅ Frontend: product.productUrl
    private Boolean active;          // ✅ Frontend: product.active
    private Double profitMargin;     // ✅ Frontend: product.profitMargin

    public static ProductResponseDTO fromEntity(Product product) {
        // Calculate profit margin
        Double margin = null;
        if (product.getPrice() != null &&
                product.getCostPrice() != null &&
                product.getPrice().compareTo(BigDecimal.ZERO) > 0) {

            BigDecimal profit = product.getPrice().subtract(product.getCostPrice());
            margin = profit.divide(product.getPrice(), 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getProductName())
                .category(product.getCategory())
                .price(product.getPrice())
                .costPrice(product.getCostPrice())
                .description(product.getProductDescription())
                .productUrl(product.getProductUrl())
                .active(product.getIsActive())
                .profitMargin(margin)
                .build();
    }
}