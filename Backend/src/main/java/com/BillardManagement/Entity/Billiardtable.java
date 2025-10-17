package com.BillardManagement.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "billiardtables")
public class Billiardtable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TableID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "ClubID", nullable = false)
    private Billardclub clubID;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "CustomerID", nullable = false)
    private Customer customerID;

    @Column(name = "TableName", nullable = false, length = 50)
    private String tableName;

    // Use a short VARCHAR for type instead of LOB; aligns with DB enum
    @Column(name = "TableType", nullable = false, length = 20)
    private String tableType;

    @Column(name = "HourlyRate", nullable = false, precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @ColumnDefault("'Available'")
    @Column(name = "TableStatus", length = 50)
    private String tableStatus;

    @Column(name = "Location", length = 100)
    private String location;

    @Column(name = "PurchaseDate")
    private LocalDate purchaseDate;

    @Column(name = "LastMaintenanceDate")
    private LocalDate lastMaintenanceDate;

    @ColumnDefault("'Good'")
    @Column(name = "TableCondition", length = 50)
    private String tableCondition;

}
