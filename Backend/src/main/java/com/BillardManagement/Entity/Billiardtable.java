package com.BillardManagement.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

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

    @Column(name = "TableType", nullable = false, length = 20)
    private String tableType;

    @Column(name = "HourlyRate", nullable = false, precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @ColumnDefault("'Available'")
    @Column(name = "TableStatus", length = 50)
    private String tableStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "ClubID", nullable = false)
    private Billardclub clubID;

    // Thêm getter để lấy CustomerID thông qua Club
    @Transient
    public Integer getCustomerID() {
        return clubID != null ? clubID.getCustomerID() : null;
    }
}