package com.BillardManagement.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BillDetailDTO {
    private Integer id;
    private String tableName;
    private String status;
    private Instant startedAt;
    private Instant endedAt;
    private BigDecimal totalHours;
    private BigDecimal totalTableCost;
    private BigDecimal totalProductCost;
    private BigDecimal discount;
    private BigDecimal finalAmount;
    private List<BillItemDTO> items;
}

