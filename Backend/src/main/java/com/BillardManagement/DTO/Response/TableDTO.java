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
public class TableDTO {
    private Integer id;
    private String name;
    private String type;
    private BigDecimal hourlyRate;
    private String status; // available | occupied | maintenance (from DB/status logic)
    private Instant startedAt; // if occupied
    private Integer activeBillId; // if occupied
    private Integer openedByEmployeeId; // owner of the active bill (if any)
}

