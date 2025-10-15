package com.BillardManagement.Entity;

import com.fasterxml.jackson.databind.deser.impl.CreatorCandidate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "billardclub")
public class Billardclub {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ClubID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "CustomerID", nullable = false)
    @JsonIgnore  // ⚠️ Thêm dòng này để tránh lỗi tuần hoàn và lỗi ByteBuddy
    private Customer customerID;

    @Column(name = "ClubName", nullable = false)
    private String clubName;

    @Lob
    @Column(name = "Address", nullable = false)
    private String address;

    @Column(name = "PhoneNumber", length = 20)
    private String phoneNumber;

    @ColumnDefault("1")
    @Column(name = "isActive")
    private Boolean isActive;

}
