package com.BillardManagement.Mapper;

import com.BillardManagement.DTO.Request.ProductRequestDTO;
import com.BillardManagement.DTO.Response.ProductResponseDTO;
import com.BillardManagement.Entity.Billardclub;
import com.BillardManagement.Entity.Customer;
import com.BillardManagement.Entity.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequestDTO dto, Customer customer, Billardclub club) {
        return Product.builder()
                .productName(dto.getName())
                .price(dto.getPrice())
                .costPrice(dto.getCostPrice() != null ? dto.getCostPrice() : BigDecimal.ZERO)
                .category(dto.getCategory())
                .productDescription(dto.getDescription())
                .productUrl(dto.getProductUrl())
                .isActive(dto.getActive() != null ? dto.getActive() : true)
                .customer(customer)
                .club(club)
                .build();
    }

    public ProductResponseDTO toResponseDTO(Product product) {
        ProductResponseDTO dto = ProductResponseDTO.builder()
                .id(product.getId())
                .clubId(product.getClub().getId())
                .clubName(product.getClub().getClubName())
                .name(product.getProductName())
                .price(product.getPrice())
                .costPrice(product.getCostPrice())
                .category(product.getCategory())
                .description(product.getProductDescription())
                .productUrl(product.getProductUrl())
                .active(product.getIsActive())
                .createdDate(product.getCreatedDate())
                .build();

        dto.calculateProfitMargin();
        return dto;
    }

    public void updateEntity(Product product, ProductRequestDTO dto) {
        product.setProductName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setCostPrice(dto.getCostPrice() != null ? dto.getCostPrice() : BigDecimal.ZERO);
        product.setCategory(dto.getCategory());
        product.setProductDescription(dto.getDescription());
        product.setProductUrl(dto.getProductUrl());
        if (dto.getActive() != null) {
            product.setIsActive(dto.getActive());
        }
    }
}
