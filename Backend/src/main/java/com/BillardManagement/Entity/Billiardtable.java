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


    @Column(name = "TableName", nullable = false, length = 50)
    private String tableName;

    @Lob
    @Column(name = "TableType", nullable = false)
    private String tableType;

    @Column(name = "HourlyRate", nullable = false, precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @ColumnDefault("'available'")
    @Lob
    @Column(name = "TableStatus")
    private String tableStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "ClubID", nullable = false)
    private Billardclub clubID;

}