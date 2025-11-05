package com.BillardManagement.DTO.Request;

//import jakarta.validation.constraints.*;
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
}