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
    // getters/setters
    // ...
    public Integer getProductId(){return productId;} public void setProductId(Integer v){productId=v;}
    public String getProductName(){return productName;} public void setProductName(String v){productName=v;}
    public String getCategory(){return category;} public void setCategory(String v){category=v;}
    public Long getQtySold(){return qtySold;} public void setQtySold(Long v){qtySold=v;}
    public BigDecimal getProfitPerUnit(){return profitPerUnit;} public void setProfitPerUnit(BigDecimal v){profitPerUnit=v;}
    public BigDecimal getTotalProfit(){return totalProfit;} public void setTotalProfit(BigDecimal v){totalProfit=v;}
}

