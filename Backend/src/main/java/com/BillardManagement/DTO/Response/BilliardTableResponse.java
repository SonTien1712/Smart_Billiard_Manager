package com.BillardManagement.DTO.Response;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class BilliardTableResponse {
    private Integer id;
    private String tableName;
    private String tableType;
    private BigDecimal hourlyRate;
    private String tableStatus;
    private Integer clubId;
    private String clubName;

    // ✅ Constructor bắt buộc JPA cần để mapping từ query
    public BilliardTableResponse(
            Integer id,
            String tableName,
            String tableType,
            BigDecimal hourlyRate,
            String tableStatus,
            Integer clubId,
            String clubName
    ) {
        this.id = id;
        this.tableName = tableName;
        this.tableType = tableType;
        this.hourlyRate = hourlyRate;
        this.tableStatus = tableStatus;
        this.clubId = clubId;
        this.clubName = clubName;
    }

    // ⚠️ Quan trọng: phải có constructor trống nữa (cho Jackson)
    public BilliardTableResponse() {}
}