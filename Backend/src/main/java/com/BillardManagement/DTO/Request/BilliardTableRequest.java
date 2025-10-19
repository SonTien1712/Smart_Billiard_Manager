package com.BillardManagement.DTO.Request;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class BilliardTableRequest {
    private String tableName;
    private String tableType;
    private BigDecimal hourlyRate;
    private String tableStatus;
    private Integer clubId; // chỉ gửi clubId
}