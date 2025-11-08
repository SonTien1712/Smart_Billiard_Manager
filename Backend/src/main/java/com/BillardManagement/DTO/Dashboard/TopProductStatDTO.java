package com.BillardManagement.DTO.Dashboard;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TopProductStatDTO {
    private Integer productId;
    private String productName;
    private String category;
    private Long qtySold;
    private BigDecimal profitPerUnit;
    private BigDecimal totalProfit;
    // getters/setters, constructor
}

