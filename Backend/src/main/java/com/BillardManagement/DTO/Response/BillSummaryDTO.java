package com.BillardManagement.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BillSummaryDTO {
    private Integer id;
    private String tableName;
    private BigDecimal amount;
    private String status;
    private Instant createdAt;
}

