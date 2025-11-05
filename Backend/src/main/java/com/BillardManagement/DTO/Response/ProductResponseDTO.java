package com.BillardManagement.DTO.Response;

import com.BillardManagement.Entity.Product; // Import entity
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
    private BigDecimal profitMargin;

    /**
     * Phương thức tĩnh (static factory method) để tạo DTO từ Entity.
     * Logic này được chuyển từ ProductMapper.java (File 4) vào đây.
     */
    public static ProductResponseDTO fromEntity(Product product) {
        if (product == null) {
            return null;
        }

        ProductResponseDTO dto = ProductResponseDTO.builder()
                .id(product.getId())
                .clubId(product.getClub() != null ? product.getClub().getId() : null)
                .clubName(product.getClub() != null ? product.getClub().getClubName() : null)
                .name(product.getProductName())
                .price(product.getPrice())
                .costPrice(product.getCostPrice())
                .category(product.getCategory())
                .description(product.getProductDescription())
                .productUrl(product.getProductUrl())
                .active(product.getIsActive())
                .createdDate(product.getCreatedDate())
                .build();

        dto.calculateProfitMargin(); // Gọi hàm tính lợi nhuận
        return dto;
    }

    public void calculateProfitMargin() {
        if (price != null && costPrice != null && costPrice.compareTo(BigDecimal.ZERO) > 0) {
            profitMargin = price.subtract(costPrice)
                    .divide(costPrice, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        } else {
            profitMargin = null; // Hoặc BigDecimal.ZERO tùy nghiệp vụ
        }
    }
}